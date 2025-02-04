using {
    cuid,
    managed
} from '@sap/cds/common';

namespace pwc.hand.ai2report;

/**
 * Represents a chat session containing multiple records.
 */
entity Chats : cuid, managed {
    title   : String; // Title of chat
    records : Composition of many Records
                  on records.chat = $self; // Composition of records within the chat
}

/**
 * Represents a message in a chat, containing a question and an answer.
 */
entity Records : cuid, managed {

    role      : String(20); // user, assistant
    prompt    : LargeString; //prompt
    content   : LargeString; // message content
    isAdopted : Boolean; // Indicates if the message was adopted
    chat      : Association to Chats; // Reference to the associated chat
    report    : Association to Reports;
}

/**
 * Represents a report generated for a message.
 */
entity Reports : cuid, managed {

    isProgramGenerated    : Integer default 0 @readonly; // Indicates if the report was generated programmatically
    isPCLGenerated        : Boolean; // Indicates if the report contains PCL (PostScript Control Language) content
    ProjectId             : String(32); // Identifier of the project associated with the report
    Text                  : String(255); // Summary or textual description of the report
    DevClass              : String(30); // Development class related to the report
    TrKorr                : String(20); // Transport request number for the report
    // jsonPCL            : LargeString; // JSON string containing PCL or structured data for the report
    fields                : Composition of many ReportFields
                                on fields.report = $self; // Composition of report fields for detailed data
    pcls                  : Composition of many PCLs
                                on pcls.report = $self; // Composition of PCLs for detailed data
    record                : Association to Records; // Reference to the associated message
    isProgramGeneratedNav : Association to ProgramGenerated
                                on isProgramGeneratedNav.code = isProgramGenerated;
}

/**
 * Represents a field within a report, providing metadata and behavior settings.
 */
entity ReportFields : cuid, managed {
    // category     : String(100); // Category of the field (e.g., input, output)
    TabFdPos     : Integer; // Position of the field in a tabular layout
    ParamText    : String(255); // Parameter description or explanation
    FieldType    : String(20); // Type of the field (e.g., text, number)
    Display      : String(1); // Indicates if the field is visible in the report
    Enterable    : String(1); // Indicates if the field is editable
    Obligatory   : String(1); // Indicates if the field is mandatory
    ValueHelp    : String(1); // Indicates if the field supports value help or search
    ToEntityText : String(60); // Textual description of the related entity
    ToEntity     : String(30); // Name of the related entity
    ToFieldText  : String(60); // Textual description of the related field
    ToField      : String(30); // Name of the related field
    Seq          : Integer; // Sort field
    report       : Association to Reports; // Reference to the associated report
    category     : Association to Category;
}

entity PCLs : cuid, managed {
    report         : Association to Reports; // Reference to the associated report
    num            : String(10); // UT number
    category       : Association to Category;
    scene          : String; //
    expectedResult : String; //
}

/**
 * Represents a parameter with a key-value pair and its description.
 */
entity Parameters : managed {
    key name        : String(255); // Parameter key or name
        value       : LargeString; // Parameter value
        description : String(1000); // Description of the parameter
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
