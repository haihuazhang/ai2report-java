using {pwc.hand.ai2report as ai} from '../db/ai';

service ChatService @(path: '/ai2report') {

    // entity Chats            as projection on ai.Chats
    //     actions {
    //         action newRecord(content : String) returns Records;
    //     }

    entity Records          as projection on ai.Records
        actions {
            action adopt() returns Records;
        }

    entity Reports          as projection on ai.Reports
        actions {
            action generateProgram();
            // @Core.OperationAvailable: in.isPCLGenerated
            action generatePCL();
            action generateCDS();
            action verify();
            action createProject()             returns Boolean;
            action newRecord(content : String) returns Records;
            action newRecordWithStream(content: String) returns Records;
            action appendToChatRecord() returns Records;
        }

    @cds.query.limit.default: 100
    entity ReportFields     as projection on ai.ReportFields;

    @cds.query.limit.max    : 1000
    @cds.query.limit.default: 100
    entity Pcls             as projection on ai.PCLs;

    @cds.query.limit.max    : 1000
    @cds.query.limit.default: 100
    entity CDSEntity        as projection on ai.CDSEntity;

    entity Parameters       as projection on ai.Parameters;
    entity SingleCheck      as projection on ai.SingleCheck;
    entity ProgramGenerated as projection on ai.ProgramGenerated;
    entity Category         as projection on ai.Category;
    entity FieldType        as projection on ai.FieldType;

    @odata.draft.enabled
    entity Files            as projection on ai.Files
        actions {
            action storeEmbeddings()  returns String;
            action deleteEmbeddings() returns String;
        };

    entity Knowledges       as
        projection on ai.Knowledges
        excluding {
            embeddings
        };

    @odata.draft.enabled
    entity RagCategory      as projection on ai.RagCategory;

    action similitarySearch(question : String, category : String, top : Integer, threshold : Decimal(5, 2)) returns array of Knowledges;

// action newChat() returns Chats;

}

annotate ChatService.Reports with @odata.draft.enabled;

annotate ChatService.Reports {
    isPCLGenerated     @Common.Label: '{i18n>isPCLGenerated}';
    isProgramGenerated @Common.Label: '{i18n>isProgramGenerated}';
};

annotate ChatService.Category {
    code @Common: {
        Text           : desc,
        TextArrangement: #TextOnly
    }
};


annotate ChatService.ReportFields {
    category   @Common: {
        Text           : categoryNav.desc,
        TextArrangement: #TextOnly,
        ValueListWithFixedValues,
        ValueList      : {
            CollectionPath: 'Category',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'category',
                ValueListProperty: 'code'
            }]
        }
    };
    FieldType  @Common: {
        ValueListWithFixedValues,
        ValueList: {
            CollectionPath: 'FieldType',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'FieldType',
                ValueListProperty: 'desc'
            }]
        }
    };
    Display    @Common: {
        ValueListWithFixedValues,
        ValueList: {
            CollectionPath: 'SingleCheck',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'Display',
                ValueListProperty: 'check'
            }]
        }
    };
    Enterable  @Common: {
        ValueListWithFixedValues,
        ValueList: {
            CollectionPath: 'SingleCheck',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'Enterable',
                ValueListProperty: 'check'
            }]
        }
    };
    Obligatory @Common: {
        ValueListWithFixedValues,
        ValueList: {
            CollectionPath: 'SingleCheck',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'Obligatory',
                ValueListProperty: 'check'
            }]
        }
    };
    ValueHelp  @Common: {
        ValueListWithFixedValues,
        ValueList: {
            CollectionPath: 'SingleCheck',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'ValueHelp',
                ValueListProperty: 'check'
            }]
        }
    };
}


annotate ChatService.Reports with @UI: {
    SelectionFields: [
        isPCLGenerated,
        isProgramGenerated,
    ],

    HeaderInfo     : {
        $Type         : 'UI.HeaderInfoType',
        TypeName      : 'Report',
        TypeNamePlural: 'Reports',
        Title         : {
            $Type: 'UI.DataField',
            Value: Text
        }
    },


    Facets         : [
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'idIdentification',
            Label : '{i18n>Basic}',
            Target: '@UI.Identification'
        },

        {
            ID    : 'Fields',
            Target: 'fields/@UI.PresentationVariant',
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Fields}',
        },
        {
            ID    : 'Pcls',
            Target: 'pcls/@UI.PresentationVariant',
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Pcls}',
        }
    ],
    LineItem       : [
        {
            $Type: 'UI.DataField',
            Value: ID,
            Label: '{i18n>ID}',
        },
        {
            $Type: 'UI.DataField',
            Value: Text,
            Label: '{i18n>Text}',
        },
        {
            $Type: 'UI.DataField',
            Value: ProjectId,
            Label: '{i18n>ProjectId}',
        },
        {
            $Type: 'UI.DataField',
            Value: DevClass,
            Label: '{i18n>DevClass}',
        },
        {
            $Type: 'UI.DataField',
            Value: TrKorr,
            Label: '{i18n>TrKorr}',
        },
        {
            $Type: 'UI.DataField',
            Value: isPCLGenerated,
            Label: '{i18n>isPCLGenerated}',
        },
        {
            $Type: 'UI.DataField',
            Value: isProgramGenerated,
            Label: '{i18n>isProgramGenerated}',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generatePCL',
            Label : '{i18n>GeneratePCL}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generateCDS',
            Label : '{i18n>GenerateCDS}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generateProgram',
            Label : '{i18n>GenerateProgram}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.verify',
            Label : '{i18n>Verify}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.createProject',
            Label : '{i18n>CreateProject}'
        }
    ],
    Identification : [
        {
            $Type: 'UI.DataField',
            Value: Text,
            Label: '{i18n>Text}',
        },
        {
            $Type: 'UI.DataField',
            Value: ProjectId,
            Label: '{i18n>ProjectId}',
        },
        {
            $Type: 'UI.DataField',
            Value: DevClass,
            Label: '{i18n>DevClass}',
        },
        {
            $Type: 'UI.DataField',
            Value: TrKorr,
            Label: '{i18n>TrKorr}',
        },
        {
            $Type: 'UI.DataField',
            Value: isPCLGenerated,
            Label: '{i18n>isPCLGenerated}',
        },
        {
            $Type: 'UI.DataField',
            Value: isProgramGenerated,
            Label: '{i18n>isProgramGenerated}',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generatePCL',
            Label : '{i18n>GeneratePCL}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generateCDS',
            Label : '{i18n>GenerateCDS}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.generateProgram',
            Label : '{i18n>GenerateProgram}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.verify',
            Label : '{i18n>Verify}'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.createProject',
            Label : '{i18n>CreateProject}'
        }
    ],
} actions {
    @Common.SideEffects: {
        TargetProperties: [
            'in/isPCLGenerated',
            'in/isProgramGenerated'
        ],
        TargetEntities  : ['in/pcls']
    }
    generatePCL;

    @Common.SideEffects: {
        TargetProperties: [
            'in/CDS1',
            'in/CDS2',
            'in/CDS3',
            'in/CDS4'
        ]
    }
    generateCDS;
};

annotate ChatService.ReportFields with @(UI.PresentationVariant: {
    SortOrder     : [{
        Property  : Seq,
        Descending: false
    }],
    Visualizations: ['@UI.LineItem']
});


annotate ChatService.ReportFields with @UI: {
    HeaderInfo    : {
        $Type         : 'UI.HeaderInfoType',
        TypeName      : 'Field',
        TypeNamePlural: 'Fields',
    },
    Facets        : [{
        $Type : 'UI.ReferenceFacet',
        ID    : 'idIdentification',
        Label : '{i18n>Basic}',
        Target: '@UI.Identification'
    }],

    Identification: [
        {
            $Type: 'UI.DataField',
            Value: category,
            Label: '{i18n>Category}',
        },
        {
            $Type: 'UI.DataField',
            Value: TabFdPos,
            Label: '{i18n>Position}',
        },
        {
            $Type: 'UI.DataField',
            Value: ParamText,
            Label: '{i18n>ParameterText}',
        },
        {
            $Type: 'UI.DataField',
            Value: Display,
            Label: '{i18n>Display}',
        },
        {
            $Type: 'UI.DataField',
            Value: Enterable,
            Label: '{i18n>Enterable}',
        },
        {
            $Type: 'UI.DataField',
            Value: Obligatory,
            Label: '{i18n>Obligatory}',
        },
        {
            $Type: 'UI.DataField',
            Value: ValueHelp,
            Label: '{i18n>ValueHelp}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToEntityText,
            Label: '{i18n>ToEntityText}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToEntity,
            Label: '{i18n>ToEntity}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToFieldText,
            Label: '{i18n>ToFieldText}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToField,
            Label: '{i18n>ToField}',
        },
        {
            $Type: 'UI.DataField',
            Value: Seq,
            Label: '{i18n>Seq}'
        }
    ],
    LineItem      : [

        {
            $Type: 'UI.DataField',
            Value: Seq,
            Label: '{i18n>Seq}'
        },
        {
            $Type: 'UI.DataField',
            Value: category,
            Label: '{i18n>Category}',
        },
        {
            $Type: 'UI.DataField',
            Value: TabFdPos,
            Label: '{i18n>Position}',
        },
        {
            $Type: 'UI.DataField',
            Value: ParamText,
            Label: '{i18n>ParameterText}',
        },
        {
            $Type: 'UI.DataField',
            Value: FieldType,
            Label: '{i18n>FieldType}',
        },
        {
            $Type: 'UI.DataField',
            Value: Display,
            Label: '{i18n>Display}',
        },
        {
            $Type: 'UI.DataField',
            Value: Enterable,
            Label: '{i18n>Enterable}',
        },
        {
            $Type: 'UI.DataField',
            Value: Obligatory,
            Label: '{i18n>Obligatory}',
        },
        {
            $Type: 'UI.DataField',
            Value: ValueHelp,
            Label: '{i18n>ValueHelp}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToEntityText,
            Label: '{i18n>ToEntityText}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToEntity,
            Label: '{i18n>ToEntity}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToFieldText,
            Label: '{i18n>ToFieldText}',
        },
        {
            $Type: 'UI.DataField',
            Value: ToField,
            Label: '{i18n>ToField}',
        }
    ]

};

annotate ChatService.Pcls {
    category @Common: {
        Text           : categoryNav.desc,
        TextArrangement: #TextOnly,
        ValueListWithFixedValues,
        ValueList      : {
            CollectionPath: 'Category',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'category',
                ValueListProperty: 'code'
            }]
        }
    };
}

annotate ChatService.Reports {
    isProgramGenerated @Common: {
        Text           : isProgramGeneratedNav.desc,
        TextArrangement: #TextOnly,
        ValueListWithFixedValues,
        ValueList      : {
            CollectionPath: 'ProgramGenerated',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'isProgramGenerated',
                ValueListProperty: 'code'
            }]
        }
    };
}


annotate ChatService.Pcls with @UI: {LineItem: [
    {
        $Type: 'UI.DataField',
        Value: num,
        Label: '{i18n>Num}',
    },
    {
        $Type: 'UI.DataField',
        Value: category,
        Label: '{i18n>Category}',
    },
    {
        $Type: 'UI.DataField',
        Value: scene,
        Label: '{i18n>Scene}',
    },
    {
        $Type: 'UI.DataField',
        Value: expectedResult,
        Label: '{i18n>expectedResult}',
    }
], };

annotate ChatService.Pcls with @(UI.PresentationVariant: {
    SortOrder     : [{
        Property  : num,
        Descending: false
    }],
    Visualizations: ['@UI.LineItem']
}, );


annotate ChatService.Parameters with {
    // value       @UI: {MultiLineText};
    description @UI: {MultiLineText};
};


annotate ChatService.Parameters @odata.draft.enabled;

annotate ChatService.Parameters with @(UI.LineItem: [
    {
        $Type: 'UI.DataField',
        Label: 'name',
        Value: name,
    },
    {
        $Type: 'UI.DataField',
        Label: 'description',
        Value: description,
    },
]);

annotate ChatService.Parameters with @(
    UI.FieldGroup #GeneratedGroup1: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Label: 'name',
                Value: name
            },
            {
                $Type: 'UI.DataField',
                Label: 'description',
                Value: description
            }
        ],
    },
    UI.Facets                     : [
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'GeneratedFacet1',
            Label : 'Description Information',
            Target: '@UI.FieldGroup#GeneratedGroup1'
        },
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'GeneratedFacet2',
            Label : 'Value Information',
            Target: 'items/@UI.LineItem'
        }
    ]
);

annotate ChatService.ParameterItems with @(
    HeaderInfo : {
        $Type         : 'UI.HeaderInfoType',
        TypeName      : 'Item',
        TypeNamePlural: 'Item',
    },
    UI.LineItem: [
        {
            $Type: 'UI.DataField',
            Label: 'name',
            Value: name,
        },
        {
            $Type: 'UI.DataField',
            Label: 'language',
            Value: language,
        },
    ]
);


annotate ChatService.Files with @(
    odata.draft.enabled,
    UI.FieldGroup #FileDetails: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: category,
                Label: 'File Category',
            },
            // {
            //     $Type: 'UI.DataField',
            //     Value: fileName,
            //     Label: 'File Name',
            // },
            {
                $Type: 'UI.DataField',
                Value: fileContent,
                Label: 'File Content',
            },
            // {
            //     $Type: 'UI.DataField',
            //     Value: mediaType,
            //     Label: 'Media Type',
            // },
            {
                $Type: 'UI.DataField',
                Value: isGenerated,
                Label: 'isGenerateEmbedding',
            },
        ],
    },
    UI.FieldGroup #Knowledges : {
        $Type: 'UI.FieldGroupType',
        Data : [],
    },
    UI.SelectionFields        : [category],
);

annotate ChatService.Files with @(
    UI.FieldGroup #GeneratedGroup: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Label: 'category',
                Value: category,
            },
            {
                $Type: 'UI.DataField',
                Value: fileContent,
            },
            // {
            //     $Type: 'UI.DataField',
            //     Label: 'mediaType',
            //     Value: mediaType,
            // },
            {
                $Type: 'UI.DataField',
                Label: 'isGeneratedEmbedding',
                Value: isGenerated,
            }
            // {
            //     $Type: 'UI.DataField',
            //     Label: 'fileContent',
            //     Value: fileContent,
            // },
        ],
    },
    UI.Facets                    : [
        {
            $Type : 'UI.CollectionFacet',
            Label : 'File Overview',
            ID    : 'fileObject',
            Facets: [{
                $Type : 'UI.ReferenceFacet',
                Label : 'File Details',
                ID    : 'FileDetails',
                Target: '@UI.FieldGroup#FileDetails',
            }, ],
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Knowledges',
            ID    : 'Knowledges',
            Target: 'knowledges/@UI.LineItem#Knowledges1',
        },
    ],
    UI.LineItem                  : [
        {
            $Type: 'UI.DataField',
            Label: 'File Category',
            Value: category,
        },
        {
            $Type: 'UI.DataField',
            Value: fileContent
        },
        // {
        //     $Type: 'UI.DataField',
        //     Label: 'File Name',
        //     Value: fileName,
        // },
        // {
        //     $Type: 'UI.DataField',
        //     Label: 'Media Type',
        //     Value: mediaType,
        // },
        {
            $Type: 'UI.DataField',
            Label: 'isGenerateEmbedding',
            Value: isGenerated,
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.storeEmbeddings',
            Label : 'Generate Embeddings'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.deleteEmbeddings',
            Label : 'Delete Embeddings'
        }
    ],
) actions {
    @Common.SideEffects: {TargetProperties: ['in/isGenerated'], }
    storeEmbeddings;
    @Common.SideEffects: {TargetProperties: ['in/isGenerated'], }
    deleteEmbeddings;
};

annotate ChatService.Files with {
    isGenerated @Common.FieldControl: #ReadOnly
};

annotate ChatService.Files with {
    category @(
        Common.FieldControl            : #Mandatory,
        Common.ValueList               : {
            $Type         : 'Common.ValueListType',
            CollectionPath: 'RagCategory',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: category,
                ValueListProperty: 'code',
            }, ],
            Label         : 'Category Help',
        },
        Common.ValueListWithFixedValues: true,
        Common.Label                   : 'category',
    )
};

annotate ChatService.Knowledges with @(
    UI.CreateHidden         : true,
    UI.DeleteHidden         : true,

    UI.LineItem #Knowledges : [
        {
            $Type: 'UI.DataField',
            Value: file_ID,
            Label: 'file_ID',
        },
        {
            $Type: 'UI.DataField',
            Value: category,
            Label: 'category',
        },
        {
            $Type: 'UI.DataField',
            Value: content,
            Label: 'content',
        },
    ],
    UI.LineItem #Knowledges1: [
        {
            $Type: 'UI.DataField',
            Value: file_ID,
            Label: 'FileID',
        },
        {
            $Type: 'UI.DataField',
            Value: category,
            Label: 'File Category',
        },
        {
            $Type: 'UI.DataField',
            Value: content,
            Label: 'File Content',
        },
        {
            $Type: 'UI.DataField',
            Value: isGeneratedEmbedding,
            Label: 'isGenerateEmbedding',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'ChatService.EntityContainer/similitarySearch',
            Label : 'Search Embeddings'
        },
    ],
);

annotate ChatService.Knowledges with {
    file @Common.FieldControl: #ReadOnly
};

annotate ChatService.Knowledges with {
    category @Common.FieldControl: #ReadOnly
};

annotate ChatService.Knowledges with {
    content @Common.FieldControl: #ReadOnly
};

// annotate ChatService.Files with {
//     mediaType @Common.FieldControl: #ReadOnly;
//     fileName @Common.FieldControl: #ReadOnly;
// };

// annotate ChatService.Files with {
//     size @Common.FieldControl: #ReadOnly
// };

annotate ChatService.RagCategory with {
    code @Common.Text: {
        $value                : desc,
        ![@UI.TextArrangement]: #TextOnly,
    }
};
