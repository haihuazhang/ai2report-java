sap.ui.define([
    "sap/ui/base/Object",
    "sap/m/BusyDialog",
    "sap/m/MessageBox",
    "sap/ui/model/odata/v4/Context",
    "sap/ui/model/odata/v4/ODataContextBinding",
    "sap/ui/model/odata/v4/ODataListBinding",
    "sap/ui/model/odata/v4/ODataModel"
], function (Object, BusyDialog, MessageBox, Context, ODataContextBinding, ODataListBinding, ODataModel) {
    "use strict";
    var ChatService = Object.extend("report.service.ChatService", {
        constructor: function () {

        },
        // 设置模型的方法
        setModel: function (model) {
            // 打印检查模型设置
            console.log("Model settings:", {
                updateGroupId: model.getUpdateGroupId(),
                groupId: model.getGroupId(),
                bindingMode: model.getDefaultBindingMode()
            });
            this.model = model;
        },
        // 提交更改的异步方法
        submitChanges: function () {
            this.model.submitBatch(this.model.getUpdateGroupId());
        },
        createEntity: function (params) {
            const { entity, binding, skipRefresh = false, atEnd = true, submitBatch = true } = params;
            return new Promise((resolve, reject) => {
                const context = binding.create(entity, skipRefresh, atEnd);
                if (submitBatch) {
                    context.created().then(() => {
                        resolve(context);
                    });
                    this.model.submitBatch(this.model.getUpdateGroupId());
                } else {
                    resolve(context);
                }
            });
        },
        // createTempEntity: function (params) {
        //     const { entity, binding, skipRefresh = false, atEnd = true, submitBatch = true } = params;
        //     // return new Promise((resolve, reject) => {
        //     const context = binding.create(entity, skipRefresh, atEnd);
        //     // if (submitBatch) {
        //     // context.created().then(() => {
        //     // resolve(context);
        //     // });
        //     // this.model.submitBatch(this.model.getUpdateGroupId());
        //     // } else {
        //     // resolve(context);
        //     // }
        // });
        // },

        // 删除实体的异步方法
        deleteEntity: function (context) {
            return new Promise((resolve, reject) => {
                context.delete().then(resolve, reject);
                this.model.submitBatch(this.model.getUpdateGroupId());
            });
        },
        // 获取完成内容的异步方法
        getCompletion: function (params) {
            return new Promise((resolve, reject) => {
                const binding = this.model.bindContext("ChatService.newRecord(...)", params.report);
                binding.setParameter("content", params.message)
                const dialog = new BusyDialog({ text: "Thinking..." });
                dialog.open();
                binding.invoke().then(() => {
                    dialog.close();
                    resolve(
                        {
                            actionContext: binding.getBoundContext(),
                            tempUserContext: params.tempUserContext
                        }
                    );
                }).catch((error) => {
                    dialog.close();
                    MessageBox.alert(error.message, {
                        title: "Error"
                    });
                    reject(error);
                });
            });
        },
        // 以流的方式获取完成内容的异步方法
        getCompletionAsStream: function (params, callback) {
            return new Promise(async (resolve, reject) => {
                // fetch(`${this.model.getServiceUrl()}getCompletionAsStream(model='${params.model}',chat='${params.chat}',personality='${params.personality}')`).then((res) => {
                //     var reader = res.body.pipeThrough(new TextDecoderStream()).getReader();
                //     while (true) {
                //         const { value, done } = await reader.read();
                //         if (done) {
                //             resolve(null);
                //             break;
                //         }
                //         const regex = /{"message":"[^{}]+?"}/g;
                //         const objects = value.match(regex);
                //         objects.forEach((object) => {
                //             try {
                //                 const data = JSON.parse(object);
                //                 if (data.message && callback) {
                //                     callback.call(this, data.message);
                //                 }
                //             } catch (error) {
                //                 console.error(error);
                //             }
                //         });
                //     }


                // });
                resolve("1");
            });
        }
    });

    return {
        // 获取单例实例的静态方法
        getInstance: function () {
            this.instance ??= new ChatService();
            return this.instance;
        }
    };



});