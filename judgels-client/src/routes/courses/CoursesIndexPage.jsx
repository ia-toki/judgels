import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import CoursesPage from './CoursesPage/CoursesPage';

export default function CoursesIndexPage() {
  return (
    <FullPageLayout narrow>
      <CoursesPage />
    </FullPageLayout>
  );
}
