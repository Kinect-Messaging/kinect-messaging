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

export const JourneyShow: React.FC = () => {
  const { id } = useParams<"id">(); // Get journeyId from URL
  console.log(`Journey ID from URL:`, id);

  // Find the journey that matches the journeyId from the hardcoded data
  const journey = hardcodedJourneys.find(j => j.id === id);
  console.log(journey)

  // If the journey does not exist, you can return a not found message or component
  if (!journey) {
    return <div>Journey not found</div>;
  }

  return (
    <Show>
      <Stack gap={1}>
        <Typography variant="h6" gutterBottom>
          Journey Details
        </Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Journey ID"}
        </Typography>
        <Typography variant="body2">{journey.id}</Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Journey Name"}
        </Typography>
        <Typography variant="body2">{journey.journeyName}</Typography>

        {/* Iterate over journeySteps and display each one */}
        {journey.journeySteps.map(step => (
          <React.Fragment key={step.seqId}>
            <Typography variant="body1" fontWeight="bold">
              {`Step ${step.seqId}: ${step.eventName}`}
            </Typography>
            <Typography variant="body2">{step.stepCondition}</Typography>
          </React.Fragment>
        ))}

        <Typography variant="body1" fontWeight="bold">
          {"Created By"}
        </Typography>
        <Typography variant="body2">{journey.auditInfo.createdBy}</Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Created Date"}
        </Typography>
        <Typography variant="body2">{journey.auditInfo.createdTime}</Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Updated By"}
        </Typography>
        <Typography variant="body2">{journey.auditInfo.updatedBy}</Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Updated Date"}
        </Typography>
        <Typography variant="body2">{journey.auditInfo.updatedTime}</Typography>
      </Stack>
    </Show>
  );
};