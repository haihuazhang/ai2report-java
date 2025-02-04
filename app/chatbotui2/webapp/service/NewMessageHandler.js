sap.ui.define([
    "sap/ui/base/Object",
    "./ChatService"
], function (Object, ChatService) {
    "use strict";

    // 定义INewMessageHandlerSettings函数，模拟接口（JavaScript中无严格接口概念，用对象结构约定参数形式）
    // function INewMessageHandlerSettings(chat, message, binding, sender, streamingCallback) {
    //     this.chat = chat;
    //     this.message = message;
    //     this.binding = binding;
    //     this.sender = sender;
    //     this.streamingCallback = streamingCallback;
    // }
    return Object.extend("chatbotui2.service.NewMessageHandler", {
        constructor: function (settings) {
            this.chat = settings.chat;
            this.message = settings.message;
            this.binding = settings.binding;
            this.sender = settings.sender;
            this.streamingCallback = settings.streamingCallback;
        },
        // 创建消息及完成回复的异步方法
        createMessageAndCompletion: function () {
            var chatService = ChatService.getInstance();
            chatService.createEntity({
                binding: this.binding,
                entity: {
                    content: this.message.trim(),
                    createdBy: this.sender
                },
                atEnd: true,
                submitBatch: false
            }).then((createdUserContext) => {
                return this.handleCompletion(createdUserContext);
            }).then((result) => {
                console.log("Message posted successfully");
                return result.tempUserContext.delete();
            }).then(() => {
                this.chat.refresh();
                chatService.model.refresh();
            }).catch(function (error) {
                console.error("Error posting message:", error);
            });
        },
        // 处理非流式完成回复的异步方法
        handleCompletion: function (createdUserContext) {
            var chatService = ChatService.getInstance();
            return chatService.getCompletion({
                chat: this.chat,
                message: this.message.trim(),
                tempUserContext: createdUserContext
            });
        },
        // 处理流式完成回复的异步方法
        handleStreamingCompletion: function () {

        }



    });
});