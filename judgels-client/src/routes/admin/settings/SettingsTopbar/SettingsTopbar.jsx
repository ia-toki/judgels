import { Topbar } from '../../../../components/Topbar/Topbar';
import { isTrainingEnabled } from '../../../../modules/webConfig';

export function SettingsTopbar({ activeItemPath }) {
  const items = [
    { path: '/admin/settings/general', title: 'General' },
    ...(isTrainingEnabled() ? [{ path: '/admin/settings/training', title: 'Training' }] : []),
  ];

  return <Topbar activeItemPath={activeItemPath} items={items} onResolveItemUrl={path => path} />;
}
