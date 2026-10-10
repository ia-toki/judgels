import { HTMLSelect } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ItemEssayCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemEssayCard/ItemEssayCard';
import { ItemMultipleChoiceCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemShortAnswerCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemShortAnswerCard/ItemShortAnswerCard';
import { ItemStatementCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemStatementCard/ItemStatementCard';
import { ItemType, itemTypeNamesMap } from '../../../../modules/api/problemBundle';
import { formatItemMediaUrls } from '../../../../modules/api/problemItem';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import { problemItemQueryOptions, updateProblemItemMutationOptions } from '../../../../modules/queries/problemItem';
import { problemStatementLanguagesQueryOptions } from '../../../../modules/queries/problemStatement';
import ProblemItemEditForm from '../ProblemItemEditForm/ProblemItemEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

import '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ProblemStatementCard.scss';

const itemCardsMap = {
  [ItemType.Statement]: ItemStatementCard,
  [ItemType.MultipleChoice]: ItemMultipleChoiceCard,
  [ItemType.ShortAnswer]: ItemShortAnswerCard,
  [ItemType.Essay]: ItemEssayCard,
};

export default function ProblemItemPage() {
  const { problemJid, itemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: item } = useQuery(problemItemQueryOptions(problemJid, itemJid, { language }));

  const updateItemMutation = useMutation(updateProblemItemMutationOptions(problemJid, itemJid, language));

  const updateItem = async data => {
    await updateItemMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Item updated.'),
    });
  };

  const renderTitle = () => {
    if (!item) {
      return 'Item';
    }
    const name = `${itemTypeNamesMap[item.type]} item`;
    return item.number ? `${name} (no. ${item.number})` : name;
  };

  const renderLanguageSelect = () => {
    return (
      <HTMLSelect aria-label="Language" value={language} onChange={e => setSelectedLanguage(e.target.value)}>
        {enabledLanguages
          .slice()
          .sort()
          .map(lang => (
            <option key={lang} value={lang}>
              {worldLanguageNamesMap[lang] || lang}
            </option>
          ))}
      </HTMLSelect>
    );
  };

  const renderContent = () => {
    if (!item) {
      return <LoadingState />;
    }
    if (config.canEdit) {
      // The form holds its own edits, so it starts over whenever the language or the saved item changes.
      return <ProblemItemEditForm key={JSON.stringify([language, item])} item={item} onSubmit={updateItem} />;
    }

    const ItemCard = itemCardsMap[item.type];
    return <ItemCard {...formatItemMediaUrls(item, problemJid)} itemNumber={item.number} disabled />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>
            <Link to={`/admin/problems/${problemJid}/items`}>Items</Link> › {renderTitle()}
          </span>
          {renderLanguageSelect()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
