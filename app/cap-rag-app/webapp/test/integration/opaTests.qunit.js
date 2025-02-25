sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'capragapp/test/integration/FirstJourney',
		'capragapp/test/integration/pages/FilesList',
		'capragapp/test/integration/pages/FilesObjectPage',
		'capragapp/test/integration/pages/KnowledgesObjectPage'
    ],
    function(JourneyRunner, opaJourney, FilesList, FilesObjectPage, KnowledgesObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('capragapp') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheFilesList: FilesList,
					onTheFilesObjectPage: FilesObjectPage,
					onTheKnowledgesObjectPage: KnowledgesObjectPage
                }
            },
            opaJourney.run
        );
    }
);