import { Topbar } from '../../../../components/Topbar/Topbar';

export function SettingsTopbar({ activeItemPath }) {
  const items = [
    { path: '/admin/settings/general', title: 'General' },
    { path: '/admin/settings/account', title: 'Account' },
  ];

  return <Topbar activeItemPath={activeItemPath} items={items} onResolveItemUrl={path => path} />;
}
