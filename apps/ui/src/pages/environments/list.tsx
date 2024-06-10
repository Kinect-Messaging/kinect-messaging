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

interface ChangeLog {
    user: string;
    time: string;
    comment: string;
}

interface Environment {
    envId: string;
    envName: string;
    journeyId: string;
    messageId: string;
    eventName: string;
    changeLog: ChangeLog[];
}

// const API_BASE_URL = process.env.REACT_APP_POSTMAN_MOCK_URL
const API_BASE_URL = 'https://ab3b979f-80e3-4626-aeee-0236b00bad7e.mock.pstmn.io'
const ENV_ENDPOINT = '/kinect/messaging/config/env';

// Setup the Axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    // headers: {
    //     'Ocp-Apim-Subscription-Key': process.env.REACT_APP_AZURE_KEY,
    //     'X-Transaction-Id': uuidv4()
    // }
});

export const EnvList: React.FC<IResourceComponentsProps> = () => {

    const [environments, setEnvironments] = useState<Environment[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchEnvironments = async () => {
            try {
                // const response = await api.get<Environment[]>(ENV_ENDPOINT);
                setEnvironments(response.data);
            } catch (err) {
                setError('Failed to fetch environments');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchEnvironments();
    }, []);

    const columns: GridColDef[] = React.useMemo(
        () => [
            {
                field: 'envId',
                headerName: 'Env ID',
                width: 100
            },
            {
                field: 'envName',
                headerName: 'Env Name',
                width: 150
            },
            {
                field: 'journeyId',
                headerName: 'Journey ID',
                width: 200,
            },
            {
                field: 'messageId',
                headerName: 'Message ID',
                width: 300,
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

    // const getJourneyNameById = (journeyId: string) => {
    //     const journey = journeys.find(j => j.id === journeyId);
    //     return journey ? journey.journeyName : '';
    // };

    // if (loading) return <div>Loading...</div>;
    // if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <div>
                <List>
                    <DataGrid
                        rows={environments}
                        columns={columns}
                        loading={loading}
                        getRowId={(row) => row.envId}
                        autoHeight
                    />
                </List>
            </div>
        </div>
    );
};
