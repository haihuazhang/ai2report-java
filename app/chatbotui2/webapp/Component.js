sap.ui.define([
    "sap/ui/core/UIComponent",
    "./model/models",
    "./service/ChatService",
    "./util/IconFonts",
    "./util/LayoutManager"
], function (UIComponent, models, ChatService, IconFonts, LayoutManager) {
    "use strict";

    return UIComponent.extend("chatbotui2.Component", {
        metadata: {
            manifest: "json"
        },

        init: function () {
            // call the base component's init function
            UIComponent.prototype.init.apply(this, arguments);

            // set device model
            this.setModel(models.createDeviceModel(), "device");
            this.setModel(models.createAppModel(), "app");

            models.createUserModel().then(function (userModel) {
                this.setModel(userModel, "user");
            }.bind(this));

            const layoutModel = models.createLayoutModel();
            this.setModel(layoutModel, "appLayout");
            LayoutManager.getInstance().setModel(layoutModel);

            ChatService.getInstance().setModel(this.getModel());

            IconFonts.register();

            // initialize the router
            this.getRouter().initialize();
        }
    });
});
