<script lang="ts">
    import Input from './lib/Input.svelte'

    const urlParams = new URLSearchParams(window.location.search)
    const options = urlParams.get("options").split('|')
    const webApp = window.Telegram.WebApp
    webApp.MainButton.onClick(() => webApp.sendData(selectedOption))
    webApp.MainButton.setParams({'text': 'Подтвердить'})

    let inputValue = ""
    let selectedOption = null
    $: filteredOptions = options.filter(option => option.toLowerCase().match(inputValue.toLowerCase()))
    $: if (selectedOption !== null) {
        webApp.MainButton.show()
    }

    function onOptionSelected(option: string) {
        selectedOption = option
    }
</script>

<style>
    p {
        color: var(--tg-theme-text-color);
    }

    .selected {
        color: var(--tg-theme-button-text-color);
        background-color: var(--tg-theme-button-color);
    }
</style>

<main>
    <Input bind:inputValue/>
    {#each filteredOptions as option}
        <p class="mx-2 px-2 py-3 w-auto text-slate-900 justify-start rounded-md {option === selectedOption ? 'selected' : ''}"
           on:click={() => onOptionSelected(option)}>{option}</p>
    {/each}
</main>
