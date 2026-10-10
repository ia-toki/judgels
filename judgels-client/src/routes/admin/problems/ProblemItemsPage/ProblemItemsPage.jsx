import { Alert, Button, HTMLSelect, HTMLTable, Intent } from '@blueprintjs/core';
import { ArrowDown, ArrowUp, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ItemType, itemTypeNamesMap } from '../../../../modules/api/problemBundle';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  createProblemItemMutationOptions,
  deleteProblemItemMutationOptions,
  moveProblemItemDownMutationOptions,
  moveProblemItemUpMutationOptions,
  problemItemsQueryOptions,
} from '../../../../modules/queries/problemItem';

import * as toastActions from '../../../../modules/toast/toastActions';

function formatPoints({ type, config }) {
  if (type === ItemType.Statement) {
    return '';
  }
  return type !== ItemType.Essay && config.penalty !== 0 ? `${config.score} (${config.penalty})` : `${config.score}`;
}

function formatAnswerFormat({ type, config }) {
  if (type === ItemType.MultipleChoice) {
    return config.choices.map(choice => choice.alias).join(' ');
  }
  return type === ItemType.ShortAnswer ? config.inputValidationRegex : '';
}

function formatAnswer({ type, config }) {
  if (type === ItemType.MultipleChoice) {
    return config.choices
      .filter(choice => choice.isCorrect)
      .map(choice => choice.alias)
      .join(' ');
  }
  return type === ItemType.ShortAnswer ? config.gradingRegex || '' : '';
}

function describeItem(item) {
  const name = `${itemTypeNamesMap[item.type]} item`;
  return item.number ? `${name} no. ${item.number}` : name;
}

export default function ProblemItemsPage() {
  const { problemJid } = useParams({ strict: false });
  const navigate = useNavigate();

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: items },
  } = useSuspenseQuery(problemItemsQueryOptions(problemJid));

  const createItemMutation = useMutation(createProblemItemMutationOptions(problemJid));
  const moveItemUpMutation = useMutation(moveProblemItemUpMutationOptions(problemJid));
  const moveItemDownMutation = useMutation(moveProblemItemDownMutationOptions(problemJid));
  const deleteItemMutation = useMutation(deleteProblemItemMutationOptions(problemJid));

  const [typeToAdd, setTypeToAdd] = useState(ItemType.Statement);
  const [itemToDelete, setItemToDelete] = useState(undefined);

  const isMutating =
    createItemMutation.isPending ||
    moveItemUpMutation.isPending ||
    moveItemDownMutation.isPending ||
    deleteItemMutation.isPending;

  const addItem = () => {
    createItemMutation.mutate(typeToAdd, {
      onSuccess: item => {
        toastActions.showSuccessToast('Item added.');
        navigate({ to: `/admin/problems/${problemJid}/items/${item.jid}` });
      },
    });
  };

  const deleteItem = async () => {
    try {
      await deleteItemMutation.mutateAsync(itemToDelete.jid, {
        onSuccess: () => toastActions.showSuccessToast('Item deleted.'),
      });
    } finally {
      setItemToDelete(undefined);
    }
  };

  const renderAddForm = () => {
    if (!config.canEdit) {
      return null;
    }
    return (
      <Flex gap={2} alignItems="center">
        <HTMLSelect aria-label="Item type" value={typeToAdd} onChange={e => setTypeToAdd(e.target.value)}>
          {Object.keys(itemTypeNamesMap).map(type => (
            <option key={type} value={type}>
              {itemTypeNamesMap[type]}
            </option>
          ))}
        </HTMLSelect>
        <Button
          text="Add"
          intent={Intent.PRIMARY}
          disabled={isMutating}
          loading={createItemMutation.isPending}
          onClick={addItem}
        />
      </Flex>
    );
  };

  const renderActions = (item, idx) => {
    if (!config.canEdit) {
      return null;
    }
    return (
      <ActionButtons justifyContent="end">
        <Button
          small
          minimal
          icon={<ArrowUp />}
          aria-label={`Move ${describeItem(item)} up`}
          disabled={idx === 0 || isMutating}
          onClick={() => moveItemUpMutation.mutate(item.jid)}
        />
        <Button
          small
          minimal
          icon={<ArrowDown />}
          aria-label={`Move ${describeItem(item)} down`}
          disabled={idx === items.length - 1 || isMutating}
          onClick={() => moveItemDownMutation.mutate(item.jid)}
        />
        <Button
          small
          minimal
          intent={Intent.DANGER}
          icon={<Trash />}
          aria-label={`Delete ${describeItem(item)}`}
          disabled={isMutating}
          onClick={() => setItemToDelete(item)}
        />
      </ActionButtons>
    );
  };

  const renderItems = () => {
    if (items.length === 0) {
      return (
        <p>
          <small>No items.</small>
        </p>
      );
    }

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>No.</th>
            <th>Type</th>
            <th>Internal note</th>
            <th>Points</th>
            <th>Format</th>
            <th>Answer</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {items.map((item, idx) => (
            <tr key={item.jid}>
              <td>{item.number}</td>
              <td>
                <Link to={`/admin/problems/${problemJid}/items/${item.jid}`}>{itemTypeNamesMap[item.type]}</Link>
              </td>
              <td>{item.meta}</td>
              <td>{formatPoints(item)}</td>
              <td>{formatAnswerFormat(item)}</td>
              <td>{formatAnswer(item)}</td>
              <td>{renderActions(item, idx)}</td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Items</span>
          {renderAddForm()}
        </h4>
      </Flex>
      {renderItems()}
      <Alert
        isOpen={itemToDelete !== undefined}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Delete"
        cancelButtonText="Cancel"
        loading={deleteItemMutation.isPending}
        onConfirm={deleteItem}
        onCancel={() => setItemToDelete(undefined)}
      >
        {itemToDelete && `Delete this ${describeItem(itemToDelete)}?`}
      </Alert>
    </Flex>
  );
}
