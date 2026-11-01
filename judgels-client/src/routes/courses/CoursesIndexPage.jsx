import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import CoursesPage from './courses/CoursesPage/CoursesPage';

export default function CoursesIndexPage() {
  return (
    <FullPageLayout narrow>
      <CoursesPage />
    </FullPageLayout>
  );
}
