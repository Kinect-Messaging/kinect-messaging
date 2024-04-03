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

interface JourneyStep {
    seqId: number;
    eventName: string;
    stepCondition: string;
    messageConfigs: Record<string, string>;
}

interface AuditInfo {
    createdBy: string;
    createdTime: string;
    updatedBy: string;
    updatedTime: string;
}

interface Journey {
    journeyId: string;
    journeyName: string;
    journeySteps: JourneyStep[];
    auditInfo: AuditInfo;
}

// const API_BASE_URL = process.env.REACT_APP_POSTMAN_MOCK_URL
// const API_BASE_URL = 'https://ab3b979f-80e3-4626-aeee-0236b00bad7e.mock.pstmn.io'
const API_BASE_URL = 'https://dev-kinect-apim-service.azure-api.net/config/v1'
const JRNY_ENDPOINT = '/kinect/messaging/config/journey';


// Setup the Axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Ocp-Apim-Subscription-Key': process.env.REACT_APP_AZURE_KEY,
        'X-Transaction-Id': uuidv4()
    }
});



export const JourneyList: React.FC<IResourceComponentsProps> = () => {



    const [journeys, setJourneys] = useState<Journey[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchJourneys = async () => {
            try {
                const response = await api.get<Journey[]>(JRNY_ENDPOINT);
                // console.log(response)
                setJourneys(response.data);
            } catch (err) {
                setError('Failed to fetch journeys');
            } finally {
                setLoading(false);
            }
        };

        fetchJourneys();
    }, []);

    const columns: GridColDef[] = React.useMemo(
        () => [
            {
                field: "journeyId",
                headerName: "Journey ID",
                type: "number",
                minWidth: 200,
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
                // minWidth: 200,
                valueGetter: ({ row }) => row.journeySteps.length,
            },
            {
                field: "createdTime",
                headerName: "Created Date",
                minWidth: 400,
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
                            <EditButton hideText recordItemId={row.journeyId} />
                            <ShowButton hideText recordItemId={row.journeyId} />
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

    // if (error) return <div>Error: {error}</div>;
    // if (loading) return <div>Loading...</div>;

    return (
        <div>
            <div>
                <List>
                    <DataGrid
                        rows={journeys}
                        columns={columns}
                        loading={loading}
                        getRowId={(row) => row.journeyId}
                        autoHeight
                    />
                </List>
            </div>
        </div>
    );
};
