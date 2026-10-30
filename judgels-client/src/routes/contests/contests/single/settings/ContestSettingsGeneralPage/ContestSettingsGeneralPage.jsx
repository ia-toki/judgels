import { Flex } from '@blueprintjs/labs';

import ContestDescriptionSection from '../ContestDescriptionSection/ContestDescriptionSection';
import ContestGeneralSection from '../ContestGeneralSection/ContestGeneralSection';

export default function ContestSettingsGeneralPage() {
  return (
    <Flex flexDirection="column" gap={8}>
      <ContestGeneralSection />
      <ContestDescriptionSection />
    </Flex>
  );
}
