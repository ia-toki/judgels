import { Flex } from '@blueprintjs/labs';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';

import './WidgetHeader.scss';

export function WidgetHeader({ title, to }) {
  return (
    <Flex className="widget-header" alignItems="center" gap={3}>
      <h3>{title}</h3>
      <ButtonLink to={to} small minimal aria-label={`See all ${title.toLowerCase()}`}>
        See all
      </ButtonLink>
    </Flex>
  );
}
