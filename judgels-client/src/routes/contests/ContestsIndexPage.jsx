import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';

import './ContestsIndexPage.scss';

export default function ContestsIndexPage() {
  return (
    <FullPageLayout>
      <div className="contests-index-page">
        <ContestsPage />
      </div>
    </FullPageLayout>
  );
}
