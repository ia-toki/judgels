import { Button, ButtonGroup } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useLocation, useNavigate } from '@tanstack/react-router';

export default function CursorPagination({ data, hasPreviousPage, hasNextPage }) {
  const location = useLocation();
  const navigate = useNavigate();

  if (!hasPreviousPage && !hasNextPage) {
    return null;
  }

  const clickNewer = () => {
    const firstId = data[0]?.id;
    if (firstId !== undefined) {
      navigate({ search: { ...location.search, before: undefined, after: firstId } });
    }
  };

  const clickOlder = () => {
    const lastId = data[data.length - 1]?.id;
    if (lastId !== undefined) {
      navigate({ search: { ...location.search, before: lastId, after: undefined } });
    }
  };

  return (
    <Flex justifyContent="end" marginY={2}>
      <ButtonGroup>
        <Button icon="chevron-left" text="Newer" disabled={!hasNextPage} onClick={clickNewer} />
        <Button rightIcon="chevron-right" text="Older" disabled={!hasPreviousPage} onClick={clickOlder} />
      </ButtonGroup>
    </Flex>
  );
}
