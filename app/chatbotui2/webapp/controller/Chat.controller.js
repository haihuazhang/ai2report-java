sap.ui.define([
    "./BaseController",
    "../util/Helper",
    "../service/ChatService",
    "../service/NewMessageHandler",
    "../util/UIHelper",
    "sap/ushell/Container"
], function (BaseController,Helper, ChatService, NewMessageHandler, UIHelper, Container) {
    "use strict";

    return BaseController.extend("chatbotui2.controller.Chat", {
        /**
         * Called when the controller is instantiated.
         */
        onInit: function () {
            this.getRouter().getRoute("chat").attachPatternMatched(this.onRouteMatched, this);
        },

        onAfterRendering: function () {
            this.addKeyboardEventsToInput();
            this.getView().byId("messageList").addEventDelegate({
                onAfterRendering: function () {
                    UIHelper.scrollToElement(this.getView().byId("listEndMarker").getDomRef(), 100);
                }.bind(this)
            });
        },

        onRouteMatched: function (event) {
            const chat = event.getParameter("arguments").chat;
            this.getView().bindElement({
                path: `/Chats(${chat})`
            });
        },

        onStreamingEnabledChange: function (event) {
            ChatService.getInstance().submitChanges();
            const toast = this.getView().byId("steamingEnabledToast");
            toast.setText(`Streaming ${event.getParameter("state") ? "enabled" : "disabled"} for chat.`);
            toast.show();
        },

        onDeleteChat: function () {
            Helper.withConfirmation("Delete Chat", "Are you sure you want to delete this chat?", function () {
                ChatService.getInstance().deleteEntity(this.getView().getBindingContext())
                    .then(function () {
                        this.getRouter().navTo("home");
                    }.bind(this))
                    .catch(function (error) {
                        console.error("Error deleting chat:", error);
                    });
            }.bind(this));
        },

        onPostMessage: function (event) {
            const message = event.getParameter("value");
            const chat = this.getView().getBindingContext();
            const binding = this.getView().byId("messageList").getBinding("items");

            const messageHandler = new NewMessageHandler({
                chat: chat,
                binding: binding,
                message: message,
                sender: this.getModel("user").getUser().displayName,
                streamingCallback: function (chunk, replyContext) {
                    if (!chunk) return;
                    replyContext.setProperty("text", `${replyContext.getProperty("text")}${chunk}`);
                    UIHelper.scrollToElement(this.getView().byId("listEndMarker").getDomRef());
                }.bind(this)
            });

            messageHandler.createMessageAndCompletion();
            // .then(function () {
            //     console.log("Message posted successfully");
            // })
            // .catch(function (error) {
            //     console.error("Error posting message:", error);
            // });
        },

        addKeyboardEventsToInput: function () {
            const input = this.getView().byId("newMessageInput");
            input.attachBrowserEvent("keydown", function (event) {
                if (event.key === "Enter" && (event.ctrlKey || event.metaKey) && input.getValue().trim() !== "") {
                    input.fireEvent("post", { value: input.getValue() });
                    input.setValue(null);
                    event.preventDefault();
                }
            });
        },
        onBtnAdoptPress: function (event) {
            event.getSource().setBusy(true);
            var context = event.getSource().getBindingContext();
            var contextBinding = this.getModel().bindContext("ChatService.adopt(...)", context, { $$inheritExpandSelect: true });
            contextBinding.invoke().finally(() => {
                event.getSource().setBusy(false);
            });
        },
        onBtnDisplayPress: function (oEvent) {
            var context = oEvent.getSource().getBindingContext();
            // var  = context.getBinding()
            Container.getServiceAsync("Navigation").then((Navigation) => {
                Navigation.navigate({
                    target: {
                        semanticObject: "report", action: "display"
                    },
                    appSpecificRoute: `Reports${this.getModel().getKeyPredicate("/Reports", {
                        ID: context.getObject().report.ID,
                        IsActiveEntity: context.getObject().report.IsActiveEntity
                    })}`
                });
            });
        }
    });
});
