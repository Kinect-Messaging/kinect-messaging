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


// Jounrey Interfaces
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

export const EnvList: React.FC<IResourceComponentsProps> = () => {

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
    const [envs] = useState<Env[]>(hardcodedEnvs);

    // Set the hardcoded data as your initial state
    const [journeys] = useState<Journey[]>(hardcodedJourneys);

    // const [journeys, setJourneys] = useState<Journey[]>([]);
    // const [envs, setEnvs] = useState<Env[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');

    // useEffect(() => {
    //     const fetchData = async () => {
    //         try {
    //             const responseJourney = await api.get<Journey[]>('/kinect/messaging/config/journey');
    //             setJourneys(responseJourney.data);
    //             const responseEnv = await api.get<Env[]>('/kinect/messaging/config/env');
    //             setEnvs(responseEnv.data)
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
                headerName: 'Env ID',
                width: 100
            },
            {
                field: 'envName',
                headerName: 'Env Name',
                width: 150
            },
            {
                field: 'journeyName',
                headerName: 'Journey Name',
                width: 200,
                valueGetter: (params) => {
                    // This function should retrieve the journey name using the journey ID
                    // For instance, you could use a state that contains all journeys' information
                    const journeyId = params.row.journeys[0]?.journeyId;
                    const journey = journeys.find((j) => j.id === journeyId);
                    return journey?.journeyName || '';
                }
            },
            {
                field: 'changeLog',
                headerName: 'Change Log Comment',
                width: 300,
                valueGetter: (params) => params.row.changeLog[0]?.comment,
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
                        rows={envs}
                        columns={columns}
                        autoHeight
                        getRowId={(row) => row.id}
                    />
                </List>
            </div>
        </div>
    );
};
