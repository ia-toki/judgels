import * as React from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import ContestFileUploadForm from '../ContestFileUploadForm/ContestFileUploadForm';

export function ContestFileUploadCard({ onSubmit }) {
  return (
    <ContentCard>
      <ContestFileUploadForm onSubmit={onSubmit} />
    </ContentCard>
  );
}
