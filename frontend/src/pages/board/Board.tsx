import {
  DragDropProvider,
  DragDropSensors,
  DragOverlay,
  SortableProvider,
  createSortable,
  closestCenter,
  maybeTransformStyle,
  Draggable,
  Droppable,
  Id,
  CollisionDetector,
  DragEventHandler,
} from "@thisbeyond/solid-dnd";
import { Link } from "solid-app-router";
import {
  batch,
  createEffect,
  createResource,
  createSignal,
  For,
  Show,
} from "solid-js";
import { createStore } from "solid-js/store";
import { fetchObservations } from "../observations/Observations";
import { api } from "../../utils/utils";
import { Status, formatStatus } from "../../models/Status";
import { ObservationDto } from "../../models/Observation";

const Sortable = (props: { item: ObservationDto }) => {
  const observation = props.item;
  const sortable = createSortable(observation.key);
  return (
    <div use:sortable classList={{ "opacity-25": sortable.isActiveDraggable }}>
      <div class="p-4 mb-2 bg-gray-50 shadow rounded-lg cursor-grab touch-none select-none">
        <div class="">
          <div class="flex items-center space-x-3">
            <Link href={`/observations/${observation.id}`}>
              <h3 class="text-gray-900 text-sm font-medium">
                {observation.key}
              </h3>
            </Link>
            <Show when={observation.immediateDanger}>
              <span class="flex-shrink-0 inline-block px-2 py-0.5 text-red-800 text-xs font-medium bg-red-100 rounded-full">
                Danger
              </span>
            </Show>
          </div>
          <p class="mt-1 text-gray-500 text-sm line-clamp-2">
            {observation.description}
          </p>
        </div>
      </div>
    </div>
  );
};

const SortableOverlay = (props: { item?: ObservationDto }) => {
  const observation = props.item;
  if (observation) {
    return (
      <div class="p-4 mb-2 bg-gray-50 shadow rounded-lg cursor-grabbing">
        <div class="">
          <div class="flex items-center space-x-3">
            <Link href={`/observations/${observation.id}`}>
              <h3 class="text-gray-900 text-sm font-medium">
                {observation.key}
              </h3>
            </Link>
            <Show when={observation.immediateDanger}>
              <span class="flex-shrink-0 inline-block px-2 py-0.5 text-red-800 text-xs font-medium bg-red-100 rounded-full">
                Danger
              </span>
            </Show>
          </div>
          <p class="mt-1 text-gray-500 text-sm line-clamp-2">
            {observation.description}
          </p>
        </div>
      </div>
    );
  }
};

const Column = (props: { id: Id; items: ObservationDto[] }) => {
  const sortable = createSortable(props.id);
  return (
    <div
      ref={sortable.ref}
      style={maybeTransformStyle(sortable.transform)}
      classList={{ "opacity-25": sortable.isActiveDraggable }}
    >
      <div class="bg-white shadow rounded-lg">
        <div class="px-4 py-2 font-medium" {...sortable.dragActivators}>
          {formatStatus(props.id as Status)}
        </div>
        <div class="px-2 grid grid-flow-row min-h-[5rem] max-h-[80vh] overflow-y-auto">
          <SortableProvider ids={props.items.map((o) => o.key)}>
            <For each={props.items}>{(item) => <Sortable item={item} />}</For>
          </SortableProvider>
        </div>
      </div>
    </div>
  );
};

const ColumnOverlay = (props: { id: Id; items: ObservationDto[] }) => {
  return (
    <div class="bg-white shadow rounded-lg">
      <div class="px-4 py-2 font-medium">
        {formatStatus(props.id as Status)}
      </div>
      <div class="px-2 grid grid-flow-row min-h-[5rem] max-h-[80vh] overflow-y-auto">
        <SortableProvider ids={props.items.map((o) => o.key)}>
          <For each={props.items}>{(item) => <Sortable item={item} />}</For>
        </SortableProvider>
      </div>
    </div>
  );
};

const Board = () => {
  const [observations] = createResource("", fetchObservations);

  const patchObservation = async (observation: ObservationDto) => {
    return await api.patch(new URL(observation._links!.self.href), {
      json: [{ op: "replace", path: "/status", value: observation.status }],
    });
  };

  const [activeItem, setActiveItem] = createSignal<Id | ObservationDto | null>(
    null
  );
  const [containers, setContainers] = createStore<Record<Id, ObservationDto[]>>(
    {
      NEW: [],
      IN_PROGRESS: [],
      DONE: [],
    }
  );
  const containerIds = () => Object.keys(containers) as Id[];
  const [containerOrder, setContainerOrder] = createSignal<Id[]>(
    Object.keys(containers)
  );

  const isContainer = (id: any) => {
    if (typeof id === "string") {
      return containerIds().includes(id);
    }
    return false;
  };

  const getContainer = (id: Id): Id => {
    for (const [key, items] of Object.entries(containers)) {
      if (items.find((item) => item.key === id)) {
        return key;
      }
    }

    throw new Error(`Could not find container for ${id}`);
  };

  const closestContainerOrItem: CollisionDetector = (
    draggable,
    droppables,
    context
  ) => {
    const closestContainer = closestCenter(
      draggable,
      droppables.filter((droppable) => isContainer(droppable.id)),
      context
    );
    if (isContainer(draggable.id)) {
      return closestContainer;
    } else if (closestContainer) {
      const containerItemIds = containers[closestContainer.id];
      const closestItem = closestCenter(
        draggable,
        droppables.filter((droppable) =>
          containerItemIds.find((item) => item.key === droppable.id)
        ),
        context
      );
      if (!closestItem) {
        return closestContainer;
      }

      if (getContainer(draggable.id) !== closestContainer.id) {
        const isLastItem =
          containerItemIds.findIndex((o) => o.key === closestItem.id) ===
          containerItemIds.length - 1;

        if (isLastItem) {
          const belowLastItem =
            draggable.transformed.center.y > closestItem.transformed.center.y;

          if (belowLastItem) {
            return closestContainer;
          }
        }
      }
      return closestItem;
    }
    return null;
  };

  const move = (
    draggable: Draggable,
    droppable: Droppable,
    onlyWhenChangingContainer = true,
    dropped = false
  ) => {
    const draggableIsContainer = isContainer(draggable.id);
    const draggableContainer = draggableIsContainer
      ? draggable.id
      : getContainer(draggable.id);
    const droppableContainer = isContainer(droppable.id)
      ? droppable.id
      : getContainer(droppable.id);

    if (
      draggableContainer != droppableContainer ||
      !onlyWhenChangingContainer
    ) {
      if (draggableIsContainer) {
        const fromIndex = containerOrder().indexOf(draggable.id);
        const toIndex = containerOrder().indexOf(droppable.id);
        const updatedOrder = containerOrder().slice();
        updatedOrder.splice(toIndex, 0, ...updatedOrder.splice(fromIndex, 1));
        setContainerOrder(updatedOrder);
      } else {
        const containerItemIds = containers[droppableContainer];
        let index = containerItemIds.findIndex((o) => o.key === droppable.id);
        if (index === -1) index = containerItemIds.length;

        const dragged = containers[draggableContainer].find(
          (o) => o.key === draggable.id
        );

        batch(() => {
          setContainers(draggableContainer, (items: ObservationDto[]) =>
            items.filter((item) => item.key !== draggable.id)
          );
          setContainers(droppableContainer, (items: ObservationDto[]) => [
            ...items.slice(0, index),
            dragged!,
            ...items.slice(index),
          ]);
          setContainers(
            droppableContainer,
            (o) => o.key === draggable.id,
            "status",
            droppableContainer as Status
          );
        });

        if (dropped) {
          patchObservation(dragged!);
        }
      }
    }
  };

  const onDragStart: DragEventHandler = ({ draggable }) => {
    if (isContainer(draggable.id)) {
      setActiveItem(draggable.id);
    } else {
      const observation = observations()?._embedded.observations?.find(
        (o) => o.key === draggable.id
      );
      if (observation) {
        setActiveItem(observation);
      }
    }
  };

  const onDragOver: DragEventHandler = ({ draggable, droppable }) => {
    if (draggable && droppable && !isContainer(draggable.id)) {
      move(draggable, droppable);
    }
  };

  const onDragEnd: DragEventHandler = ({ draggable, droppable }) => {
    if (draggable && droppable) {
      move(draggable, droppable, false, true);
    }
  };

  createEffect(() => {
    setContainers({
      NEW:
        observations()?._embedded.observations?.filter(
          (o) => o.status === "NEW"
        ) ?? [],
      IN_PROGRESS:
        observations()?._embedded.observations?.filter(
          (o) => o.status === "IN_PROGRESS"
        ) ?? [],
      DONE:
        observations()?._embedded.observations?.filter(
          (o) => o.status === "DONE"
        ) ?? [],
    });
  });

  return (
    <div class="overflow-x-scroll md:overflow-x-visible">
      <DragDropProvider
        onDragStart={onDragStart}
        onDragOver={onDragOver}
        onDragEnd={onDragEnd}
        collisionDetector={closestContainerOrItem}
      >
        <DragDropSensors />
        <div class="grid grid-flow-col auto-cols-fr gap-2 min-w-[720px]">
          <SortableProvider ids={containerOrder()}>
            <For each={containerOrder()}>
              {(key) => <Column id={key} items={containers[key]} />}
            </For>
          </SortableProvider>
        </div>
        <DragOverlay>
          {
            (isContainer(activeItem()) ? (
              <ColumnOverlay
                id={activeItem()}
                items={containers[activeItem()]}
              />
            ) : (
              <SortableOverlay item={activeItem()} />
            )) as Element
          }
        </DragOverlay>
      </DragDropProvider>
    </div>
  );
};

export default Board;
