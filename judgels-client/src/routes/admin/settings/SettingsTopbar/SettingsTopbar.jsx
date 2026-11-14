import { Topbar } from '../../../../components/Topbar/Topbar';
import { isTrainingEnabled } from '../../../../modules/webConfig';

export function SettingsTopbar({ activeItemPath }) {
  // Without training, General is the only tab, so there is nothing to switch between.
  if (!isTrainingEnabled()) {
    return null;
  }

  const items = [
    { path: '/admin/settings/general', title: 'General' },
    { path: '/admin/settings/training', title: 'Training' },
  ];

  return <Topbar activeItemPath={activeItemPath} items={items} onResolveItemUrl={path => path} />;
}
