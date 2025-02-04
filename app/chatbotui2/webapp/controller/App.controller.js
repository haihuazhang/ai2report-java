sap.ui.define([
    "./BaseController",
    "../util/Helper"
], function (BaseController, Helper) {
    "use strict";

    return BaseController.extend("chatbotui2.controller.App", {
        /**
         * Called when the controller is instantiated.
         */
        onInit: function () {
            // apply content density mode to root view
            this.getView().addStyleClass(Helper.getContentDensityClass());
        }
    });
});