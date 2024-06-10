import { Autocomplete, Box, Button, Select, TextField } from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import { useParams } from "react-router-dom";
import { IResourceComponentsProps } from "@refinedev/core";
import { Edit, useAutocomplete } from "@refinedev/mui";
import { useForm } from "@refinedev/react-hook-form";
import React from "react";
import { Controller } from "react-hook-form";

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

export const JourneyEdit: React.FC = () => {

    const { id } = useParams<"id">()
    const journey = hardcodedJourneys.find(j => j.id === id)

    const {
        saveButtonProps,
        refineCore: { formLoading },
        register,
        formState: { errors },
    } = useForm({})

    if (!journey) {
        return <div>Journey not found</div>
    }

    return (
        <Edit isLoading={formLoading} saveButtonProps={saveButtonProps}>
            <Box
                component="form"
                sx={{ display: "flex", flexDirection: "column" }}
                autoComplete="off"
            >
                <TextField
                    label="Journey Name"
                    defaultValue={journey.journeyName}
                />

                <TextField
                    label="Created By"
                    defaultValue={journey.auditInfo.createdBy}
                />

                <TextField
                    label="Updated By"
                    defaultValue={journey.auditInfo.updatedBy}
                />

                <Button type="submit">Save Changes</Button>

            </Box>
        </Edit>
    )
}