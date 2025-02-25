using embeddingService as service from '../../srv/rag/service';

annotate service.Files with @(
    odata.draft.enabled,
    UI.FieldGroup #FileDetails : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : category,
                Label : 'File Category',
            },
            {
                $Type : 'UI.DataField',
                Value : fileName,
                Label : 'File Name',
            },
            {
                $Type : 'UI.DataField',
                Value : fileContent,
                Label : 'File Content',
            },
            {
                $Type : 'UI.DataField',
                Value : mediaType,
                Label : 'Media Type',
            },
            {
                $Type : 'UI.DataField',
                Value : isGenerated,
                Label : 'isGenerateEmbedding',
            },
        ],
    },
    UI.FieldGroup #Knowledges : {
        $Type : 'UI.FieldGroupType',
        Data : [
        ],
    },
    UI.SelectionFields : [
        category,
    ],
);

annotate service.Files with @(
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
                Label: 'fileName',
                Value: fileName,
            },
            {
                $Type: 'UI.DataField',
                Label: 'mediaType',
                Value: mediaType,
            },
            {
                $Type: 'UI.DataField',
                Label: 'isGeneratedEmbedding',
                Value: isGenerated,
            },
            {
                $Type: 'UI.DataField',
                Label: 'fileContent',
                Value: fileContent,
            },
        ],
    },
    UI.Facets                    : [
        {
            $Type : 'UI.CollectionFacet',
            Label : 'File Overview',
            ID : 'fileObject',
            Facets : [
                {
                    $Type : 'UI.ReferenceFacet',
                    Label : 'File Details',
                    ID : 'FileDetails',
                    Target : '@UI.FieldGroup#FileDetails',
                },
            ],
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Knowledges',
            ID : 'Knowledges',
            Target : 'knowledges/@UI.LineItem#Knowledges1',
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
            Label: 'File Name',
            Value: fileName,
        },
        {
            $Type: 'UI.DataField',
            Label: 'Media Type',
            Value: mediaType,
        },
        {
            $Type: 'UI.DataField',
            Label: 'isGenerateEmbedding',
            Value: isGenerated,
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'embeddingService.storeEmbeddings',
            Label : 'Generate Embeddings'
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'embeddingService.deleteEmbeddings',
            Label : 'Delete Embeddings'
        }
    ],
) actions {
    @Common.SideEffects: {TargetProperties: ['in/isGenerated'], }
    storeEmbeddings;
    @Common.SideEffects: {TargetProperties: ['in/isGenerated'], }
    deleteEmbeddings;
};
annotate service.Files with {
    isGenerated @Common.FieldControl : #ReadOnly
};

annotate service.Files with {
    category @(
        Common.FieldControl : #Mandatory,
        Common.ValueList : {
            $Type : 'Common.ValueListType',
            CollectionPath : 'RagCategory',
            Parameters : [
                {
                    $Type : 'Common.ValueListParameterInOut',
                    LocalDataProperty : category,
                    ValueListProperty : 'code',
                },
            ],
            Label : 'Category Help',
        },
        Common.ValueListWithFixedValues : true,
        Common.Label : 'category',
        )
};

annotate service.Knowledges with @(
    UI.CreateHidden: true,
    UI.DeleteHidden:true,

    UI.LineItem #Knowledges : [
        {
            $Type : 'UI.DataField',
            Value : file_ID,
            Label : 'file_ID',
        },
        {
            $Type : 'UI.DataField',
            Value : category,
            Label : 'category',
        },
        {
            $Type : 'UI.DataField',
            Value : content,
            Label : 'content',
        },
    ],
    UI.LineItem #Knowledges1 : [
        {
            $Type : 'UI.DataField',
            Value : file_ID,
            Label : 'FileID',
        },
        {
            $Type : 'UI.DataField',
            Value : category,
            Label : 'File Category',
        },
        {
            $Type : 'UI.DataField',
            Value : content,
            Label : 'File Content',
        },
        {
            $Type : 'UI.DataField',
            Value : isGeneratedEmbedding,
            Label : 'isGenerateEmbedding',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action: 'embeddingService.EntityContainer/similitarySearch',
            Label : 'Search Embeddings'
        },
    ],
);

annotate service.Knowledges with {
    file @Common.FieldControl : #ReadOnly
};

annotate service.Knowledges with {
    category @Common.FieldControl : #ReadOnly
};

annotate service.Knowledges with {
    content @Common.FieldControl : #ReadOnly
};

annotate service.Files with {
    mediaType @Common.FieldControl : #ReadOnly
};

annotate service.Files with {
    size @Common.FieldControl : #ReadOnly
};

annotate service.RagCategory with {
    code @Common.Text : {
        $value : desc,
        ![@UI.TextArrangement] : #TextOnly,
    }
};

