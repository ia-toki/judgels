import { Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import classNames from 'classnames';

import './ContentRow.scss';

export function ContentRow({ className, to, leading, title, meta, badges, status }) {
  return (
    <Flex asChild alignItems="center" gap={3}>
      <Link className={classNames('content-row', className)} to={to}>
        {leading}
        <Flex className="content-row__main" flexDirection="column" flexGrow={1} gap={3}>
          <Flex flexDirection="column" gap={1}>
            <h4 className="content-row__title" data-key="name">
              {title}
            </h4>
            {meta && (
              <div className="content-row__meta" data-key="meta">
                {meta}
              </div>
            )}
          </Flex>
          {badges && (
            <Flex className="content-row__badges" flexWrap="wrap" gap={1}>
              {badges}
            </Flex>
          )}
        </Flex>
        {status && (
          <div className="content-row__status" data-key="status">
            {status}
          </div>
        )}
      </Link>
    </Flex>
  );
}
