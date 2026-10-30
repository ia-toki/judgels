import { Flex } from '@blueprintjs/labs';

import { EditableSections } from '../../../../../../components/EditableSections/EditableSections';
import ContestDescriptionSection from '../ContestDescriptionSection/ContestDescriptionSection';
import ContestGeneralSection from '../ContestGeneralSection/ContestGeneralSection';

export default function ContestSettingsGeneralPage() {
  return (
    <EditableSections>
      <Flex flexDirection="column" gap={8}>
        <ContestGeneralSection />
        <ContestDescriptionSection />
      </Flex>
    </EditableSections>
  );
}
