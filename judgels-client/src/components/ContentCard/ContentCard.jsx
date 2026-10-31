import { Card } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import classNames from 'classnames';

import './ContentCard.scss';

export function ContentCard({ id, className, compact, framed, header, title, subtitle, action, children }) {
  const cardClassName = classNames(className, 'content-card', {
    'content-card--compact': compact,
    'content-card--framed': framed,
  });

  if (!header && !title) {
    return (
      <Flex asChild flexDirection="column" gap={2}>
        <Card id={id} className={cardClassName}>
          {children}
        </Card>
      </Flex>
    );
  }

  const renderHeader = () => {
    if (header) {
      return header;
    }

    if (subtitle) {
      return (
        <>
          <Flex justifyContent="space-between" alignItems="baseline">
            <h3 className="content-card__title">{title}</h3>
            <small>{subtitle}</small>
          </Flex>
          <hr />
        </>
      );
    }
    if (action) {
      return (
        <>
          <Flex gap={2} alignItems="baseline">
            <h3 className="content-card__title">{title}</h3>
            {action}
          </Flex>
          <hr />
        </>
      );
    }
    return (
      <>
        <h3 className="content-card__title">{title}</h3>
        <hr />
      </>
    );
  };

  return (
    <Card id={id} className={cardClassName}>
      {renderHeader()}
      <Flex flexDirection="column" gap={2}>
        {children}
      </Flex>
    </Card>
  );
}
