import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import CoursesPage from './courses/CoursesPage/CoursesPage';

import './CoursesIndexPage.scss';

export default function CoursesIndexPage() {
  return (
    <FullPageLayout>
      <div className="courses-index-page">
        <CoursesPage />
      </div>
    </FullPageLayout>
  );
}
