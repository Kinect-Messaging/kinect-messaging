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


// Message Interface
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

// Journey Interface
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

// Env Interface
interface ChangeLogEntry {
    user: string;
    time: string;
    comment: string;
}

interface JourneyReference {
    journeyId: string;
    messageIds: string[];
}

interface Env {
    id: string;
    envName: string;
    journeys: JourneyReference[];
    changeLog: ChangeLogEntry[];
}

// interface Journey {
//     journeyId: string;
//     journeyName: string;
//     journeySteps: [];
//     auditInfo: [];
// }

const hardcodedEnvs: Env[] = [
    {
        "id": "env_1",
        "envName": "Development",
        "journeys": [
            {
                "journeyId": "1",
                "messageIds": [
                    "1",
                    "2"
                ]
            }
        ],
        "changeLog": [
            {
                "user": "Developer",
                "time": "2024-03-04T00:18:57Z",
                "comment": "Setup development environment"
            }
        ]
    },
    {
        "id": "env_2",
        "envName": "Production",
        "journeys": [
            {
                "journeyId": "1",
                "messageIds": [
                    "1",
                    "2"
                ]
            }
        ],
        "changeLog": [
            {
                "user": "Ops",
                "time": "2024-04-01T11:00:00Z",
                "comment": "Deployed User Onboarding journey to production"
            }
        ]
    }
];

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

const calculateTotalMessageIds = (envs: any[]) => {
    return envs.map(env => {
        // Reduce the journeys array to sum up the length of messageIds arrays
        const totalMessageIds = env.journeys.reduce((total: any, journey: { messageIds: string | any[]; }) => {
            return total + journey.messageIds.length;
        }, 0);
        return {
            ...env, // Spread the rest of the environment object
            totalMessageIds // Include the total count
        };
    });
};

export const EnvShow: React.FC = () => {

    const { id } = useParams<"id">()
    // Find the env that matches the envId from the hardcoded data
    const env = hardcodedEnvs.find(e => e.id === id);
    // Find the num of messages that matches the messageId from the hardcoded data
    const enrichedEnvs = calculateTotalMessageIds(hardcodedEnvs);
    // Find the journey that matches the journeyId from the hardcoded data
    const journey = hardcodedJourneys.find(j => j.id === env?.journeys[0].journeyId);

    if (!env) {
        return <div>Env not found</div>
    }


    return (
        <Show>
            <Stack gap={1}>
                <Typography variant="h6" gutterBottom>
                    Environment Details
                </Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Environment ID"}
                </Typography>
                <Typography variant="body2">{env.id}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Environment Name"}
                </Typography>
                <Typography variant="body2">{env.envName}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Journey Name"}
                </Typography>
                <Typography variant="body2">{journey?.journeyName}</Typography>

                <Typography variant="body1" fontWeight="bold">
                    {"Change Log"}
                </Typography>
                <Typography variant="body2">{"User: " + env.changeLog[0].user}</Typography>
                <Typography variant="body2">{"Time: " + env.changeLog[0].time}</Typography>
                <Typography variant="body2">{"Comment: " + env.changeLog[0].comment}</Typography>

            </Stack>
        </Show>
    )
}