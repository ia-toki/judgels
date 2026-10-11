import { Checkbox } from '@blueprintjs/core';

import { isTLX } from '../../../../conf';

import './RoleCheckboxes.scss';

export function getRoleFields() {
  const fields = [
    {
      key: 'account',
      label: 'System',
      description: 'Manages every user, the roles users hold and the site settings.',
    },
    { key: 'problem', label: 'Problem', description: 'Sees, creates and manages every problem.' },
    { key: 'contest', label: 'Contest', description: 'Creates and manages every contest.' },
  ];
  if (isTLX()) {
    fields.push({ key: 'training', label: 'Training', description: 'Manages training, including every lesson.' });
  }
  return fields;
}

// `value` holds the checked roles, as `{ [key]: true }`.
export function RoleCheckboxes({ value, onChange, disabledKeys = [] }) {
  return (
    <div className="role-checkboxes">
      {getRoleFields().map(({ key, label, description }) => (
        <Checkbox
          key={key}
          checked={!!value[key]}
          disabled={disabledKeys.includes(key)}
          onChange={e => onChange({ ...value, [key]: e.target.checked })}
        >
          {label} admin
          <small className="role-checkboxes__description">{description}</small>
        </Checkbox>
      ))}
    </div>
  );
}
