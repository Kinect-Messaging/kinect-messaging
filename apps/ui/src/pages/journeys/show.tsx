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
import React, { useEffect, useState } from 'react';
import axios from "axios";

const API_BASE_URL = 'https://ab3b979f-80e3-4626-aeee-0236b00bad7e.mock.pstmn.io';
const JRNY_ENDPOINT = '/kinect/messaging/config/journey';

const api = axios.create({
  baseURL: API_BASE_URL,
  // Add headers if needed for your API requests
});

export const JourneyShow: React.FC = () => {

  const { id } = useParams<{ id: string }>(); // Ensuring you get the "id" param as defined in the route
  const [journey, setJourney] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchJourney = async () => {
      try {
        const response = await api.get(`${JRNY_ENDPOINT}/${id}`);
        setJourney(response.data);
      } catch (err) {
        setError('Failed to fetch journey details.');
      } finally {
        setLoading(false);
      }
    };

    fetchJourney();
  }, [id]);

  // if (loading) {
  //   return <div>Loading...</div>;
  // }

  // if (error) {
  //   return <div>Error: {error}</div>;
  // }

  // if (!journey) {
  //   return <div>Journey not found</div>;
  // }

  return (
    <Show>
      <Stack gap={1}>
        <Typography variant="h6" gutterBottom>
          Journey Details
        </Typography>

        <Typography variant="body1" fontWeight="bold">
          {"Journey Name"}
        </Typography>
        <Typography variant="body2">{journey.journeyName}</Typography>
      </Stack>
    </Show>
  );
};