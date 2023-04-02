<script lang="ts">
    import Input from './lib/Input.svelte'

    const urlParams = new URLSearchParams(window.location.search)
    const options = urlParams.get("options").split('|')
    const noneOption = urlParams.get("none")
    const noneConfirm = urlParams.get("noneConfirm")
    const webApp = window.Telegram.WebApp
    webApp.MainButton.onClick(() => webApp.sendData(JSON.stringify(selectedOption)))
    webApp.MainButton.setParams({'text': 'Подтвердить'})

    let inputValue = ""
    let selectedOption = null
    $: filteredOptions = options.filter(option => option.toLowerCase().includes(inputValue.toLowerCase()))
    $: if (selectedOption !== null) {
        webApp.MainButton.show()
    }

    function onOptionSelected(option: string) {
        selectedOption = option
    }

    function onNoneClick() {
        webApp.showConfirm(noneConfirm, () => webApp.sendData(JSON.stringify(null)))
    }
</script>

<style>
    p, button {
        color: var(--tg-theme-text-color);
    }

    .selected {
        color: var(--tg-theme-button-text-color);
        background-color: var(--tg-theme-button-color);
    }
</style>

<main>
    <Input bind:inputValue/>
    <button class="m-2 w-full text-slate-900" on:click={onNoneClick}>{noneOption}</button>
    {#each filteredOptions as option}
        <p class="mx-2 px-2 py-3 w-auto text-slate-900 justify-start rounded-md {option === selectedOption ? 'selected' : ''}"
           on:click={() => onOptionSelected(option)}>{option}</p>
    {/each}
</main>
