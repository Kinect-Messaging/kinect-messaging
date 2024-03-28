import { Stack, Typography } from "@mui/material";
import { IResourceComponentsProps, useOne, useShow } from "@refinedev/core";
import { useParams } from "react-router-dom";
import {
    DateField,
    MarkdownField,
    NumberField,
    Show,
    TextFieldComponent as TextField,
} from "@refinedev/mui";
import React from "react";

interface EmailConfig {
    senderAddress: string;
    subject: string;
}

interface Message {
    id: string;
    messageName: string;
    journeyId: string;
    messageCondition: string;
    messageStatus: string;
    emailConfig: EmailConfig;
}

// interface Journey {
//     journeyId: string;
//     journeyName: string;
//     journeySteps: [];
//     auditInfo: [];
// }

interface JourneyStep {
    seqId: number;
    eventName: string;
    stepCondition: string;
    messageIds: string[];
}

interface AuditInfo {
    createdBy: string;
    createdTime: string;
    updatedBy: string;
    updatedTime: string;
}

interface Journey {
    id: string;
    journeyName: string;
    journeySteps: JourneyStep[];
    auditInfo: AuditInfo;
}


const hardcodedMessages: Message[] = [
    {
        "id": "1",
        "messageName": "Welcome Email",
        "journeyId": "1",
        "messageCondition": "user.signup_complete == true",
        "messageStatus": "Active",
        "emailConfig": {
            "senderAddress": "welcome@example.com",
            "subject": "Welcome to Our Service!"
        }
    },
    {
        "id": "2",
        "messageName": "Feedback Request",
        "journeyId": "1",
        "messageCondition": "user.first_login == true",
        "messageStatus": "Active",
        "emailConfig": {
            "senderAddress": "feedback@example.com",
            "subject": "We Value Your Feedback!"
        }
    }
];

const hardcodedJourneys: Journey[] = [
    {
        "id": "1",
        "journeyName": "User Onboarding",
        "journeySteps": [
            {
                "seqId": 1,
                "eventName": "SignUpComplete",
                "stepCondition": "user.sign_up_complete=true",
                "messageIds": ["1"]
            },
            {
                "seqId": 2,
                "eventName": "FirstLogin",
                "stepCondition": "user.first_login=true",
                "messageIds": ["2"]
            }
        ],
        "auditInfo": {
            "createdBy": "Unit Test 1",
            "createdTime": "2024-01-01T08:00:00Z",
            "updatedBy": "Unit Test 2",
            "updatedTime": "2024-01-01T09:00:00Z"
        }
    }
];

export const MessageShow: React.FC = () => {
    const { id } = useParams<"id">(); // Get journeyId from URL
    // console.log(`Journey ID from URL:`, id);

    
    // Find the message that matches the messageId from the hardcoded data
    const message = hardcodedMessages.find(m => m.id === id);
    
    // Find the journey that matches the journeyId from the hardcoded data
    const journey = hardcodedJourneys.find(j => j.id === message?.journeyId);

    // If the journey does not exist, you can return a not found message or component
    if (!message) {
        return <div>Journey not found</div>;
    }

    return (
        <Show>
            <Stack gap={1}>
                <Typography variant="h6" gutterBottom>
                    Message Details
                </Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Message ID"}
                </Typography>
                <Typography variant="body2">{message.id}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Message Name"}
                </Typography>
                <Typography variant="body2">{message.messageName}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Journey Name"}
                </Typography>
                <Typography variant="body2">{journey?.journeyName}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Message Status"}
                </Typography>
                <Typography variant="body2">{message.messageStatus}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Email Config"}
                </Typography>
                <Typography variant="body2">{"Sender Address: " + message.emailConfig.senderAddress}</Typography>
                <Typography variant="body2">{"Email Subject: " + message.emailConfig.subject}</Typography>

            </Stack>
        </Show>
    );
};