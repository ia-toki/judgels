import { Checkbox } from '@blueprintjs/core';

import { isTLX } from '../../../../conf';

export function getRoleFields() {
  const fields = [
    { key: 'account', label: 'System' },
    { key: 'problem', label: 'Problem' },
    { key: 'contest', label: 'Contest' },
  ];
  if (isTLX()) {
    fields.push({ key: 'training', label: 'Training' });
  }
  return fields;
}

// `value` holds the checked roles, as `{ [key]: true }`.
export function RoleCheckboxes({ value, onChange, disabledKeys = [] }) {
  return (
    <div>
      {getRoleFields().map(({ key, label }) => (
        <Checkbox
          key={key}
          label={`${label} admin`}
          checked={!!value[key]}
          disabled={disabledKeys.includes(key)}
          onChange={e => onChange({ ...value, [key]: e.target.checked })}
        />
      ))}
    </div>
  );
}
