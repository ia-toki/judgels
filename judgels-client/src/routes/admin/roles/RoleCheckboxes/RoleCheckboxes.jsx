import { Checkbox } from '@blueprintjs/core';

import { isTLX } from '../../../../conf';
import { UserRole } from '../../../../modules/api/userRole';

export function getRoleFields() {
  const fields = [
    { role: UserRole.SystemAdmin, label: 'System' },
    { role: UserRole.ProblemAdmin, label: 'Problem' },
    { role: UserRole.ContestAdmin, label: 'Contest' },
  ];
  if (isTLX()) {
    fields.push({ role: UserRole.TrainingAdmin, label: 'Training' });
  }
  return fields;
}

// `value` holds the checked roles, as `{ [role]: true }`.
export function RoleCheckboxes({ value, onChange, disabledRoles = [] }) {
  return (
    <div>
      {getRoleFields().map(({ role, label }) => (
        <Checkbox
          key={role}
          label={`${label} admin`}
          checked={!!value[role]}
          disabled={disabledRoles.includes(role)}
          onChange={e => onChange({ ...value, [role]: e.target.checked })}
        />
      ))}
    </div>
  );
}
