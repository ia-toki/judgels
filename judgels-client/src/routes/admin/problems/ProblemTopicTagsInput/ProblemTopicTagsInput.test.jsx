import { HTMLTable } from '@blueprintjs/core';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Field, Form } from 'react-final-form';

import { ProblemTopicTagsInput } from './ProblemTopicTagsInput';

describe('ProblemTopicTagsInput', () => {
  const topicTags = ['topic-ad hoc', 'topic-graph', 'topic-graph: bipartite', 'topic-graph: shortest path'];

  let values;

  const renderComponent = initialTags => {
    render(
      <Form onSubmit={() => {}} initialValues={{ topicTags: initialTags }}>
        {({ values: formValues }) => {
          values = formValues;
          return (
            <HTMLTable>
              <tbody>
                <Field component={ProblemTopicTagsInput} name="topicTags" label="Tags" topicTags={topicTags} />
              </tbody>
            </HTMLTable>
          );
        }}
      </Form>
    );
    return userEvent.setup();
  };

  const checkbox = name => screen.getByRole('checkbox', { name });

  test('shows child tags by their own name', () => {
    renderComponent([]);
    expect(screen.getAllByRole('checkbox').map(c => c.name)).toEqual(topicTags);
    expect(checkbox('bipartite')).toBeInTheDocument();
  });

  test('checking a child checks its parent, which becomes indeterminate', async () => {
    const user = renderComponent([]);

    await user.click(checkbox('bipartite'));

    expect(values.topicTags).toEqual(['topic-graph: bipartite', 'topic-graph']);
    expect(checkbox('graph')).toBeChecked();
    expect(checkbox('graph')).toBePartiallyChecked();
    expect(checkbox('shortest path')).toBeEnabled();
  });

  test('checking a parent alone clears and disables its children', async () => {
    const user = renderComponent(['topic-graph', 'topic-graph: bipartite']);

    await user.click(checkbox('graph'));
    expect(values.topicTags).toEqual([]);
    expect(checkbox('bipartite')).toBeEnabled();

    await user.click(checkbox('graph'));
    expect(values.topicTags).toEqual(['topic-graph']);
    expect(checkbox('graph')).not.toBePartiallyChecked();
    expect(checkbox('bipartite')).toBeDisabled();
    expect(checkbox('shortest path')).toBeDisabled();
  });

  test('unchecking the last child keeps its parent', async () => {
    const user = renderComponent(['topic-graph', 'topic-graph: bipartite']);

    await user.click(checkbox('bipartite'));

    expect(values.topicTags).toEqual(['topic-graph']);
  });
});
