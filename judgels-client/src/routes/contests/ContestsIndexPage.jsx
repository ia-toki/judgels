import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';

export default function ContestsIndexPage() {
  return (
    <FullPageLayout narrow>
      <ContestsPage />
    </FullPageLayout>
  );
}
