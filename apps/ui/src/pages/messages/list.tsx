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
import { v4 as uuidv4 } from 'uuid';

interface EmailConfig {
    targetSystem: string;
    senderAddress: string;
    subject: string;
}

interface AuditInfo {
    createdBy: string;
    createdTime: string;
    updatedBy: string;
    updatedTime: string;
}

interface Message {
    messageId: string;
    messageName: string;
    messageVersion: number;
    emailConfig: EmailConfig[];
    journeyId: string;
    auditInfo: AuditInfo[];
}


// const API_BASE_URL = process.env.REACT_APP_POSTMAN_MOCK_URL
const API_BASE_URL = 'https://ab3b979f-80e3-4626-aeee-0236b00bad7e.mock.pstmn.io'
const MESSAGE_ENDPOINT = '/kinect/messaging/config/message';

// Setup the Axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Ocp-Apim-Subscription-Key': process.env.REACT_APP_AZURE_KEY,
        'X-Transaction-Id': uuidv4()
    }
});

export const MessageList: React.FC<IResourceComponentsProps> = () => {

    const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                // const response = await api.get<Message[]>(MESSAGE_ENDPOINT);
                // console.log(response.data)
                setMessages(response.data);
            } catch (err) {
                setError('Failed to fetch messages');
            } finally {
                setLoading(false);
            }
        };

        fetchMessages();
    }, []);

    // const getJourneyNameById = (journeyId: string) => {
    //     const journey = journeys.find(j => j.id === journeyId);
    //     return journey ? journey.journeyName : '';
    // };

    const columns: GridColDef[] = React.useMemo(
        () => [
            {
                field: 'messageId',
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
                field: 'journeyId',
                headerName: 'Journey ID',
                width: 200,
                // valueGetter: (params) => {
                //     return getJourneyNameById(params.row.journeyId);
                // }
            },
            {
                field: 'emailSubject',
                headerName: 'Email Subject',
                width: 250,
                valueGetter: (params) => params.row.emailConfig[0].subject,
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
        []
        // [journeys]
    );

    // if (error) return <div>Error: {error}</div>;
    // if (loading) return <div>Loading...</div>;

    return (
        <div>
            <div>
                <List>
                    <DataGrid
                        rows={messages}
                        columns={columns}
                        loading={loading}
                        getRowId={(row) => row.messageId}
                        autoHeight
                    />
                </List>
            </div>
        </div>
    );
};
