sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'report/test/integration/FirstJourney',
		'report/test/integration/pages/ReportsList',
		'report/test/integration/pages/ReportsObjectPage',
		'report/test/integration/pages/ReportFieldsObjectPage'
    ],
    function(JourneyRunner, opaJourney, ReportsList, ReportsObjectPage, ReportFieldsObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('report') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheReportsList: ReportsList,
					onTheReportsObjectPage: ReportsObjectPage,
					onTheReportFieldsObjectPage: ReportFieldsObjectPage
                }
            },
            opaJourney.run
        );
    }
);