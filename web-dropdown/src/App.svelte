<script lang="ts">
    import Input from './lib/Input.svelte'

    const urlParams = new URLSearchParams(window.location.search)
    const cities = urlParams.get("options").split('|')
    const webApp = window.Telegram.WebApp
    webApp.MainButton.onClick(() => webApp.sendData(selectedCity))
    webApp.MainButton.setParams({'text': 'Подтвердить'})

    let inputValue = ""
    let selectedCity = null
    $: filteredCities = cities.filter(city => city.toLowerCase().match(inputValue.toLowerCase()))
    $: if (selectedCity !== null) {
        webApp.MainButton.show()
    }

    function onCitySelected(city: string) {
        selectedCity = city
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
    {#each filteredCities as city}
        <p class="mx-2 px-2 py-3 w-auto text-slate-900 justify-start rounded-md {city === selectedCity ? 'selected' : ''}"
           on:click={() => onCitySelected(city)}>{city}</p>
    {/each}
</main>
