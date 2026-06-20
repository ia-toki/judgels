import { Alignment, Button, MenuItem } from '@blueprintjs/core';
import { CaretDown } from '@blueprintjs/icons';
import { Select } from '@blueprintjs/select';

import { isTLX } from '../../../../conf';

import './RoleSelect.scss';

const roleValues = ['', 'ADMIN'];
export const roleNames = { '': '-', ADMIN: 'Admin' };

export function getRoleFields() {
  const fields = [
    { key: 'account', label: 'Account' },
    { key: 'problem', label: 'Problem' },
    { key: 'contest', label: 'Contest' },
  ];
  if (isTLX()) {
    fields.push({ key: 'training', label: 'Training' });
  }
  return fields;
}

export function emptyRoles() {
  const roles = {};
  for (const { key } of getRoleFields()) {
    roles[key] = '';
  }
  return roles;
}

export function RoleSelect({ label, value, onChange }) {
  return (
    <Select
      items={roleValues}
      activeItem={value}
      filterable={false}
      popoverProps={{ usePortal: false }}
      onItemSelect={onChange}
      itemRenderer={(item, { handleClick, modifiers }) => (
        <MenuItem active={modifiers.active} key={item || 'none'} text={roleNames[item]} onClick={handleClick} />
      )}
    >
      <Button
        className="role-select-button"
        alignText={Alignment.LEFT}
        aria-label={label}
        text={roleNames[value]}
        rightIcon={<CaretDown />}
      />
    </Select>
  );
}
