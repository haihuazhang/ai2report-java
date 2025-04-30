sap.ui.define(
    ["sap/fe/core/AppComponent", "./model/models", "./service/ChatService","./util/IconFonts"],
    function (Component, models, ChatService,IconFonts) {
        "use strict";

        return Component.extend("report.Component", {
            metadata: {
                manifest: "json"
            },

            init: function () {
                // 调用父类的init方法
                Component.prototype.init.apply(this, arguments);

                // 初始化并设置UserModel
                models.createUserModel().then(function (userModel) {
                    this.setModel(userModel, "user");
                }.bind(this));

                // 初始化 ChatService
                ChatService.getInstance().setModel(this.getModel());
                IconFonts.register();
            }
        });
    }
);