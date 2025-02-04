using {pwc.hand.ai2report as ai} from '../db/ai';

service ChatService @(path: '/ai2report') {

    entity Chats            as projection on ai.Chats
        actions {
            action newRecord(content : String) returns Records;
        }

    entity Records          as projection on ai.Records
        actions {
            action adopt() returns Records;
        }

    entity Reports          as projection on ai.Reports
        actions {
            action generateProgram();
            @Core.OperationAvailable: in.isPCLGenerated
            action generatePCL();
            action verify();
            action createProject() returns Boolean;
        }

    @cds.query.limit.default: 100
    entity ReportFields     as projection on ai.ReportFields;

    @cds.query.limit.max    : 1000
    @cds.query.limit.default: 100
    entity Pcls             as projection on ai.PCLs;

    entity Parameters       as projection on ai.Parameters;
    entity SingleCheck      as projection on ai.SingleCheck;
    entity ProgramGenerated as projection on ai.ProgramGenerated;
    entity Category         as projection on ai.Category;
    entity FieldType        as projection on ai.FieldType;
    action newChat() returns Chats;

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
        Text           : category.desc,
        TextArrangement: #TextOnly,
        ValueListWithFixedValues,
        ValueList      : {
            CollectionPath: 'Category',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'category_code',
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
            Label : '{i18n> }'
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
    generatePCL
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
            Value: category_code,
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
            Value: category_code,
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
        Text           : category.desc,
        TextArrangement: #TextOnly,
        ValueListWithFixedValues,
        ValueList      : {
            CollectionPath: 'Category',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: 'category_code',
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
        Value: category_code,
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
    value       @UI: {MultiLineText};
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
        Label: 'value',
        Value: value,
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
    UI.Facets                     : [{
        $Type : 'UI.ReferenceFacet',
        ID    : 'GeneratedFacet1',
        Label : 'Description Information',
        Target: '@UI.FieldGroup#GeneratedGroup1'
    }]
);
