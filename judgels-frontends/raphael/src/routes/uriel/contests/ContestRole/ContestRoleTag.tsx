import { Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContestRole } from 'modules/api/uriel/contestWeb';

import { contestRoleColor } from './ContestRoleColor';

export interface ContestRoleTagProp {
  role?: ContestRole;
}

export const ContestRoleTag = (props: ContestRoleTagProp) => (
  <div>
    {props.role && contestRoleColor[props.role] && <Tag intent={contestRoleColor[props.role]}>{props.role}</Tag>}
  </div>
);
