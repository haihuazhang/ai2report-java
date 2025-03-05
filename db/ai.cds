using {
    cuid,
    managed
} from '@sap/cds/common';

// using {  zui_zye9001_001 as zye } from '../srv/external/zui_zye9001_001';

namespace pwc.hand.ai2report;

/**
 * Represents a chat session containing multiple messages.
 */
// entity Order as projection on zye.CDSType;

// entity Chats : cuid, managed {
//     title   : String; // Title of chat
//     // prompt  : LargeString; //prompt
//     records : Composition of many Records
//                   on records.chat = $self; // Composition of messages within the chat
// }

/**
 * Represents a record in a chat.
 */

entity Records : cuid, managed {
    report    : Association to Reports; // Reference to the associated chat
    role      : String; // user, assistant
    content   : LargeString; // message content
    isAdopted : Boolean; // Indicates if the record was adopted
    chatTime : Timestamp; // Time stamp of the record
}

/**
 * Represents a report generated for a message.
 */
entity Reports : cuid, managed {
    // record             : Association to Records; // Reference to the associated message
    isProgramGenerated    : Integer default 0     @readonly; // Indicates if the report was generated programmatically
    isPCLGenerated        : Boolean default false @readonly; // Indicates if the report contains PCL (PostScript Control Language) content
    ProjectId             : String(10); // Identifier of the project associated with the report
    Text                  : String(255); // Summary or textual description of the report
    DevClass              : String(30); // Development class related to the report
    TrKorr                : String(20); // Transport request number for the report
    CDS1                  : LargeString;
    CDS2                  : LargeString;
    CDS3                  : LargeString;
    CDS4                  : LargeString;
    // jsonPCL            : LargeString; // JSON string containing PCL or structured data for the report
    records               : Composition of many Records
                                on records.report = $self;
    fields                : Composition of many ReportFields
                                on fields.report = $self; // Composition of report fields for detailed data
    pcls                  : Composition of many PCLs
                                on pcls.report = $self; // Composition of PCLs for detailed data
    cdsNav                : Composition of many CDSEntity
                                on cdsNav.report = $self;

    isProgramGeneratedNav : Association to ProgramGenerated
                                on isProgramGeneratedNav.code = isProgramGenerated;
}

/**
 * Represents a field within a report, providing metadata and behavior settings.
 */
entity ReportFields : cuid, managed {
    report              : Association to Reports; // Reference to the associated report
    categoryNav         : Association to Category
                              on categoryNav.code = category; // Category of the field (e.g., input, output)
    category            : String(100); // Category of the field (e.g., input, output)
    TabFdPos            : Integer; // Position of the field in a tabular layout
    ParamText           : String(255); // Parameter description or explanation
    FieldType           : String(20); // Type of the field (e.g., text, number)
    Display             : String(1); // Indicates if the field is visible in the report
    Enterable           : String(1); // Indicates if the field is editable
    Obligatory          : String(1); // Indicates if the field is mandatory
    ValueHelp           : String(1); // Indicates if the field supports value help or search
    ToEntityText        : String(60); // Textual description of the related entity
    ToEntity            : String(30); // Name of the related entity
    ToFieldText         : String(60); // Textual description of the related field
    ToField             : String(30); // Name of the related field
    IsKey               : String(1); //
    RequiresCalculation : String(1); //
    CaculationLogic     : String; //
    ValueHelpTable      : String(30); //
    ValueHelpField      : String(30); //
    Seq                 : Integer; // Sort field
}

entity PCLs : cuid, managed {
    report         : Association to Reports; // Reference to the associated report
    num            : String(10); // UT number
    categoryNav    : Association to Category
                         on categoryNav.code = category; // Category of the field (e.g., input, output)
    category       : String(100); // Category of the field (e.g., input, output)
    scene          : String; //
    expectedResult : String; //
}

entity CDSEntity : cuid, managed {
    report   : Association to Reports; // Reference to the associated report
    category : String(1); // 1 2 3 4
    CDS1     : LargeString;
    CDS2     : LargeString;
    CDS3     : LargeString;
    CDS4     : LargeString;
}


/**
 * Represents a parameter with a key-value pair and its description.
 */
entity Parameters : managed {
    key name        : String(60); // Parameter key or name
        description : String(1000) @UI.MultiLineText; // Description of the parameter
        items       : Composition of many ParameterItems
                          on items.name = name; // Composition of parameter items for detailed data
}

entity ParameterItems : managed {
    key name     : String(60); // Parameter key or name
    key language : String(60); // Language code
        value    : LargeString @UI.MultiLineText; // Parameter value
}

entity SingleCheck {
    key check : String(1);
}

entity FieldType {
    key code : String(20);
        desc : localized String(20);
}

entity Category {
    key code : String(100);
        desc : localized String(100);
}

entity ProgramGenerated {
    key code : Integer;
        desc : localized String(100);
}

entity Files : cuid, managed {
    category    : String;
    fileName    : String;
    size        : String;

    @Core.IsMediaType: true
    mediaType   : String;
    isGenerated : Boolean;

    @Core.MediaType  : mediaType  @Core.ContentDisposition.Filename: fileName
    fileContent : LargeBinary;
    knowledges  : Composition of many Knowledges
                      on knowledges.file = $self;
}

entity Knowledges : cuid, managed {
    file                 : Association to Files;
    category             : String;
    content              : LargeString;
    isGeneratedEmbedding : Boolean;
    embeddings           : Vector(768);
}

entity RagCategory {
    key code : String(100);
        desc : localized String(100);
}
