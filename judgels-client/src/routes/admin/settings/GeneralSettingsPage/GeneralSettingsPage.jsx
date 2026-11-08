import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { EditableSections } from '../../../../components/EditableSections/EditableSections';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { settingsQueryOptions } from '../../../../modules/queries/setting';
import { SettingsTopbar } from '../SettingsTopbar/SettingsTopbar';
import { AppSection } from './AppSection';
import { HomeSection } from './HomeSection';
import { SessionSection } from './SessionSection';

export default function GeneralSettingsPage() {
  const { data } = useQuery(settingsQueryOptions());

  return (
    <ContentCard title="Settings">
      <Flex flexDirection="column" gap={5}>
        <SettingsTopbar activeItemPath="/admin/settings/general" />
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
      </Flex>
    </ContentCard>
  );
}
