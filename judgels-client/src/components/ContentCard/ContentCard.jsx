import { Card } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import classNames from 'classnames';
import { createContext, useContext } from 'react';

import { CardHeading } from '../CardHeading/CardHeading';

import './ContentCard.scss';

// True directly inside a page's main card: a titled card there is a section of the page, with a heading bar.
export const PageContentContext = createContext(false);

export function ContentCard({ id, className, compact, framed, header, title, subtitle, action, children }) {
  const isPageSection = useContext(PageContentContext) && !!title;

  const cardClassName = classNames(className, 'content-card', {
    'content-card--compact': compact,
    'content-card--framed': framed,
    'content-card--page-section': isPageSection,
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

    return (
      <>
        <CardHeading title={title} action={subtitle ? <small>{subtitle}</small> : action} />
        {!isPageSection && <hr />}
      </>
    );
  };

  return (
    <Card id={id} className={cardClassName}>
      <div className="content-card__heading">{renderHeader()}</div>
      <Flex className="content-card__body" flexDirection="column" gap={2}>
        <PageContentContext.Provider value={false}>{children}</PageContentContext.Provider>
      </Flex>
    </Card>
  );
}
