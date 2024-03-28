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

export const JourneyList: React.FC<IResourceComponentsProps> = () => {

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
    const [journeys] = useState<Journey[]>(hardcodedJourneys);

    const columns: GridColDef[] = React.useMemo(
        () => [
            {
                field: "id",
                headerName: "Journey ID",
                type: "number",
                minWidth: 150,
            },
            {
                field: "journeyName",
                headerName: "Journey Name",
                minWidth: 200,
                flex: 1,
            }, 
            {
                field: "journeySteps",
                headerName: "Journey Steps",
                type: "number",
                minWidth: 200,
                valueGetter: ({ row }) => row.journeySteps.length,
            },
            {
                field: "createdAt",
                headerName: "Created Date",
                minWidth: 200,
                valueGetter: ({ row }) => row.auditInfo.createdTime,
                renderCell: (params) => <DateField value={params.value} />,
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
                            {/* {console.log(row.journeyId)} */}
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
    );


    // const [journeys, setJourneys] = useState<Journey[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');

    // useEffect(() => {
    //     const fetchJourneys = async () => {
    //         try {
    //             const response = await api.get<Journey[]>('/kinect/messaging/config/journey');
    //             setJourneys(response.data);
    //         } catch (err) {
    //             setError('Failed to fetch journeys');
    //         } finally {
    //             setLoading(false);
    //         }
    //     };

    //     fetchJourneys();
    // }, []);

    // if (loading) return <div>Loading...</div>;
    // if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <div>
                <List>
                    <DataGrid
                        rows={journeys}
                        columns={columns}
                        autoHeight
                        getRowId={(row) => row.id}
                    />
                </List>
            </div>
        </div>
    );
};
