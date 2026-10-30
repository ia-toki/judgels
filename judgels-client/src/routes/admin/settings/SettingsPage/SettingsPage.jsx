import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { EditableSections } from '../../../../components/EditableSections/EditableSections';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { settingsQueryOptions } from '../../../../modules/queries/setting';
import { AppSection } from './AppSection';
import { HomeSection } from './HomeSection';
import { SessionSection } from './SessionSection';

export default function SettingsPage() {
  const { data } = useQuery(settingsQueryOptions());

  return (
    <ContentCard title="Settings">
      {data ? (
        <EditableSections>
          <Flex flexDirection="column" gap={8}>
            <AppSection app={data.app} />
            <HomeSection home={data.home} />
            <SessionSection session={data.session} />
          </Flex>
        </EditableSections>
      ) : (
        <LoadingState />
      )}
    </ContentCard>
  );
}
