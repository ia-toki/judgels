import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import SingleProfileDataLayout from './single/SingleProfileDataLayout';
import { SingleProfileLayout, singleProfileRoutes } from './single/SingleProfileRoutes';

function ProfilesLayout() {
  return (
    <div>
      <SingleProfileDataLayout />
      <SingleProfileLayout />
    </div>
  );
}

const ProfilesLayoutWithBreadcrumb = withBreadcrumb('Profiles')(ProfilesLayout);

export const profilesRoutes = [
  {
    path: ':username',
    element: <ProfilesLayoutWithBreadcrumb />,
    children: singleProfileRoutes,
  },
];
