import * as React from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';

import ContestFileUploadForm, { ContestFileUploadFormData } from '../ContestFileUploadForm/ContestFileUploadForm';

export interface ContestFileUploadCardProps {
  onSubmit: (data: ContestFileUploadFormData) => Promise<void>;
}

export const ContestFileUploadCard = (props: ContestFileUploadCardProps) => (
  <ContentCard>
    <ContestFileUploadForm onSubmit={props.onSubmit} />
  </ContentCard>
);
