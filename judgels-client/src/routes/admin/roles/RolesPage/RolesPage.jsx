import { Intent } from '@blueprintjs/core';
import { Upload } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { RolesSection } from '../RolesSection/RolesSection';

export default function RolesPage() {
  const { data: response } = useQuery(userRolesQueryOptions());

  const renderAction = () => {
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/roles/-/upsert">
        <Upload />
        <span>Upsert roles</span>
      </ButtonLink>
    );
  };

  return (
    <ContentCard title="Roles" action={renderAction()}>
      {response ? <RolesSection roles={response} /> : <LoadingState />}
    </ContentCard>
  );
}
