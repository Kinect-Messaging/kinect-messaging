import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { IResourceComponentsProps, useMany } from "@refinedev/core";
import {
    DateField,
    DeleteButton,
    EditButton,
    List,
    MarkdownField,
    ShowButton,
    useDataGrid,
} from "@refinedev/mui";

import React, { useEffect, useState } from 'react';
import axios from 'axios';

// const API_BASE_URL = process.env.REACT_APP_POSTMAN_MOCK_URL
// const API_BASE_URL = 'https://f3e508e8-3cf8-4ee6-90b5-391b84fb9fef.mock.pstmn.io'

// Setup the Axios instance
// const api = axios.create({
//     baseURL: API_BASE_URL,
// });

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

export const MessageList: React.FC<IResourceComponentsProps> = () => {

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

    // Set the hardcoded data as your initial state
    const [messages] = useState<Message[]>(hardcodedMessages);

    // Set the hardcoded data as your initial state
    const [journeys] = useState<Journey[]>(hardcodedJourneys);

    // const [journeys, setJourneys] = useState<Journey[]>([]);
    // const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');

    // useEffect(() => {
    //     const fetchData = async () => {
    //         try {
    //             const responseJourney = await api.get<Journey[]>('/kinect/messaging/config/journey');
    //             setJourneys(responseJourney.data);
    //             const responseMessage = await api.get<Message[]>('/kinect/messaging/config/message');
    //             setMessages(responseMessage.data)
    //         } catch (err) {
    //             setError('Failed to fetch journeys');
    //         } finally {
    //             setLoading(false);
    //         }
    //     };

    //     fetchData();
    // }, []);

    const columns: GridColDef[] = React.useMemo(
        () => [
            {
                field: 'id',
                headerName: 'Message ID',
                width: 120,
                type: 'number',
            },
            {
                field: 'messageName',
                headerName: 'Message Name',
                width: 200,
            },
            {
                field: 'journeyName',
                headerName: 'Journey Name',
                width: 200,
                valueGetter: (params) => {
                    return getJourneyNameById(params.row.journeyId);
                }
            },
            {
                field: 'emailSubject',
                headerName: 'Email Subject',
                width: 250,
                valueGetter: (params) => params.row.emailConfig.subject,
            },
            {
                field: "actions",
                headerName: "Actions",
                sortable: false,
                renderCell: function render({ row }) {
                    return (
                        <>
                            <EditButton hideText recordItemId={row.id} />
                            <ShowButton hideText recordItemId={row.id} />
                            {/* <DeleteButton hideText recordItemId={row.id} /> */}
                        </>
                    );
                },
                align: "center",
                headerAlign: "center",
                minWidth: 80,
            },
        ],
        [journeys]
    );

    const getJourneyNameById = (journeyId: string) => {
        const journey = journeys.find(j => j.id === journeyId);
        return journey ? journey.journeyName : '';
    };

    // if (loading) return <div>Loading...</div>;
    // if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <div>
                <List>
                    <DataGrid
                        rows={messages}
                        columns={columns}
                        autoHeight
                        getRowId={(row) => row.id}
                    />
                </List>
            </div>
        </div>
    );
};
