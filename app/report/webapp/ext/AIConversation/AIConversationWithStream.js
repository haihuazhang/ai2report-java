sap.ui.define([
    "sap/m/MessageToast",
    "report/util/Helper",
    "report/service/ChatService",
    "report/service/NewMessageHandler",
    "report/util/UIHelper",
], function (MessageToast, Helper, ChatService, NewMessageHandler, UIHelper) {
    'use strict';

    return {
        // 添加对话状态标志
        _isProcessing: false,

        onPressAIConversationWithStream: function (oEvent) {
            this.pDialogWithStream ??= this.loadFragment({
                name: "report.ext.AIConversation.fragment.AIConversationWithStream"
            });

            const that = this;

            this.pDialogWithStream.then((oDialog) => {
                that._dialogWithStream = oDialog;
                
                // 添加列表数据加载完成的事件处理
                const messageList = oDialog.getContent()[0].getContent()[0].getItems()[0];
                messageList.getBinding("items").attachDataReceived(() => {
                    this.scrollToListEnd();
                });
                
                oDialog.open();
            });
            
            this.onAIConversationClose = function (oEvent) {
                this.pDialogWithStream.then((oDialog) => oDialog.close());
            };

            this.onPostMessageWithStream = function (event) {
                // 如果正在处理中，禁止新的对话
                if (this._isProcessing) {
                    MessageToast.show("Please wait for the current conversation to complete");
                    return;
                }

                if (!event.getParameter("value")) {
                    return;
                }

                const messageList = this._dialogWithStream.getContent()[0].getContent()[0].getItems()[0];
                const feedInput = this._dialogWithStream.getContent()[0].getFooter().getContent()[0];

                // 设置处理状态
                this._isProcessing = true;
                feedInput.setEnabled(false);

                const message = event.getParameter("value");
                const report = this.getEditFlow().getView().getBindingContext();
                const binding = messageList.getBinding("items");
                const userModel = this.getEditFlow().getView().getModel("user");

                const messageHandler = new NewMessageHandler({
                    report: report,
                    binding: binding,
                    message: message,
                    sender: userModel.getUser().displayName,
                    onCreatedEmptyAssistantMessage: function (replyContext) {
                        // 设置Busy状态                        
                        const messageListItem = messageList.getItems().find(item =>
                            item.getBindingContext() === replyContext
                        );
                        if (messageListItem) {
                            messageListItem.setLoading(true);
                        }
                        this.scrollToListEnd();
                    }.bind(this),
                    streamingCallback: function (chunk, replyContext) {
                        if (!chunk) return;

                        // replyContext.setProperty("content", `${replyContext.getProperty("content")}${chunk}`);
                        replyContext.setProperty("content", chunk);

                        const messageListItem = messageList.getItems().find(item =>
                            item.getBindingContext() === replyContext
                        );
                        if (messageListItem) {
                            messageListItem.setLoading(false);
                            messageListItem.invalidate();
                        }

                        // const listEndMarker = this._dialogWithStream.getContent()[0].getContent()[0].getItems()[1];
                        // UIHelper.scrollToElement(listEndMarker.getDomRef());
                        this.scrollToListEnd();
                    }.bind(this),
                    onComplete: function () {
                        // 对话完成后恢复状态
                        this._isProcessing = false;
                        feedInput.setEnabled(true);
                        feedInput.setValue("");
                    }.bind(this)
                });

                messageHandler.createMessageAndCompletion(true,
                    sap.ui.require.toUrl(`${this.getEditFlow().getAppComponent().getManifest()["sap.app"].id}/api/chat/stream`)
                    );
            };

            this.onBtnAdoptPress = function (event) {
                event.getSource().setBusy(true);
                var context = event.getSource().getBindingContext();
                var contextBinding = this.getEditFlow().getView().getModel().bindContext("ChatService.adopt(...)", context, { $$inheritExpandSelect: true });
                contextBinding.invoke().finally(() => {
                    event.getSource().setBusy(false);
                    // refresh Reports Context
                    this.getEditFlow().getView().getBindingContext().refresh();
                });
            };

            this.onPressSyncChangesToChatList = function (event) {
                const binding = this.getEditFlow().getView().getModel().bindContext("ChatService.appendToChatRecord(...)",
                    this.getEditFlow().getView().getBindingContext()
                    // { $$inheritExpandSelect: true }
                );
                binding.invoke().then(() => {
                    // refresh Reports Context
                    this.getEditFlow().getView().getBindingContext().refresh();
                });
            };
            this.scrollToListEnd = function () {
                if (!this._dialogWithStream) {
                    return;
                }
                
                const listEndMarker = this._dialogWithStream.getContent()[0].getContent()[0].getItems()[1];
                if (listEndMarker && listEndMarker.getDomRef()) {
                    UIHelper.scrollToElement(listEndMarker.getDomRef());
                } else {
                    // 如果元素还没有渲染完成，延迟执行
                    setTimeout(() => this.scrollToListEnd(), 100);
                }
            };

        }
    };
});
