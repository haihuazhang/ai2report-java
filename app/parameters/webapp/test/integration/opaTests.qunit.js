sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'parameters/test/integration/FirstJourney',
		'parameters/test/integration/pages/ParametersList',
		'parameters/test/integration/pages/ParametersObjectPage'
    ],
    function(JourneyRunner, opaJourney, ParametersList, ParametersObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('parameters') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheParametersList: ParametersList,
					onTheParametersObjectPage: ParametersObjectPage
                }
            },
            opaJourney.run
        );
    }
);